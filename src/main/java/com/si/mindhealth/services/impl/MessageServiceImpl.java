package com.si.mindhealth.services.impl;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MessageRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.dtos.response.MessageResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.entities.Message;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.Sender;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.exceptions.ForbiddenException;
import com.si.mindhealth.repositories.MessageRepository;
import com.si.mindhealth.services.FeedbackService;
import com.si.mindhealth.services.GeminiService;
import com.si.mindhealth.services.MessageService;
import com.si.mindhealth.services.MoodEntryService;
import com.si.mindhealth.services.UserService;
import com.si.mindhealth.services.nlp.CrisisDetector;
import com.si.mindhealth.utils.NormalizeInput;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserService userService;
    private final MessageRepository messageRepository;
    private final GeminiService geminiService;
    private final MoodEntryService moodEntryService;
    private final FeedbackService feedbackService;
    private final String PROMPT = "Bạn tên là Lucy, là trợ lý của một ứng dụng quản lý nhật ký tâm trạng MindHealth, hãy luôn trả lời bằng Tiếng Việt, không trả lời những câu hỏi ngoài phạm vi, nếu người dùng có nhu cầu cần được chỉ cách ghi nhật ký, hãy chỉ người dùng ấn vào dấu cộng ở thanh tabbar phía dưới, ngoài ra có thể trả lời người dùng về các chủ đề liên quan đến tâm trạng, trả lời ngắn gọn 2-3 câu, kèm 1 câu mang tính tích cực và 1 emoji phù hợp, đừng mở đầu bằng chào, và đây là tin nhắn người dùng gửi cho bạn: ";
    private final String OPEN_PROMPT = "Bạn tên là Lucy, là trợ lý của một ứng dụng quản lý nhật ký tâm trạng MindHealth, hãy luôn trả lời bằng Tiếng Việt, hãy mở đầu câu chuyện với người dùng , đây là các chủ đề gần đây mà người dùng đang gặp phải: ";

    // User luu tin nhan -> chatbot phan hoi,
    // User vao muc nhan tin chatbot -> chatbot tao 1 cau mo dau

    @Override
    @Transactional
    public MessageResponseDTO create(MessageRequestDTO request, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        Message message = new Message();
        message.setUser(user);
        message.setContent(request.getContent());
        message.setSender(Sender.USER);

        String normed = NormalizeInput.normalizeForMatch(message.getContent());
        boolean isCrisis = CrisisDetector.hasCrisisSignal(normed);

        Message responseFromAI = this.getResponse(messageRepository.save(message), user);
        MessageResponseDTO response = new MessageResponseDTO(responseFromAI, isCrisis, true);
        return response;
    }

    @Override
    public PageResponseDTO<MessageResponseDTO> getList(Map<String, String> params, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        // Kiểm tra hôm nay có tin nhắn nào chưa
        Instant startOfDay = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        boolean hasTodayMsg = messageRepository.existsByUserAndCreatedAtAfter(user, startOfDay);

        // Nếu chưa có tin nhắn hôm nay -> tạo phản hồi mở đầu
        if (!hasTodayMsg) {
            try {
                MoodEntry mood = moodEntryService.getLastest(user);
                String openingPrompt = "Xin chào! Mình là Lucy 🤗. Hôm nay bạn cảm thấy thế nào?";
                if (mood != null) {
                    String userInfoPrompt = "Người dùng họ tên trên hệ thống là: " + user.getFirstName() + " " + user.getLastName() + ", giới tính là: " + (user.getGender() == false ? "nam" : "nữ.");
                    String topics = "";
                    for (var topic : mood.getMoodResult().getTopics()) {
                        topics += topic.getTopic().getLabel();
                    }
                    openingPrompt = geminiService.generateText(userInfoPrompt + OPEN_PROMPT + topics);
                }
                Message botMessage = new Message();
                botMessage.setUser(user);
                botMessage.setSender(Sender.BOT);
                botMessage.setContent(openingPrompt);
                messageRepository.save(botMessage);
            } catch (Exception e) {

            }
        }

        int page = NumberUtils.toInt(params.get("page"), 0);
        int size = NumberUtils.toInt(params.get("size"), 10);
        size = Math.min(Math.max(size, 1), 100);

        String sortBy = params.getOrDefault("sort", "createdAt");
        String order = params.getOrDefault("order", "DESC");
        Sort.Direction dir = "ASC".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));

        Page<Message> pageData = messageRepository.findByUser(user, pageable);

        return new PageResponseDTO<>(
        pageData.map(m -> {
            boolean hasFeedback = (m.getSender() == Sender.BOT)
                    && feedbackService.get(TargetType.MESSAGE, m.getId(), principal) != null;
            MessageResponseDTO dto = new MessageResponseDTO(m);
            dto.setCanFeedback(!hasFeedback);
            return dto;
        })
    );
    }

    public Message getResponse(Message userMessage, User user) {
        List<Message> latest4UserMsgs =
            messageRepository.findTop4ByUserAndSenderOrderByCreatedAtDesc(user, Sender.USER);
        latest4UserMsgs.remove(0);
        String msgs = "";
        for(var ms: latest4UserMsgs) {
            msgs = msgs + ms.getContent() + "///";
        }
        String userInfoPrompt = "Người dùng họ tên trên hệ thống là: " + user.getFirstName() + " " + user.getLastName() + ", giới tính là: " + (user.getGender() == false ? "nam" : "nữ.");
        String altProm = ", và đây là 3 đoạn tin nhắn trước đó và gần nhất do người dùng gửi" + msgs +"\n Lưu ý 3 tin nhắn trên chỉ dùng để tăng tính chính xác khi trả lời, hãy tập trung trả lời vào tin nhắn của người dùng gửi cho bạn thay vì lịch sử tin nhắn.";

        String output = geminiService.generateText(userInfoPrompt + PROMPT + userMessage.getContent() + altProm);

        Message m = new Message();
        m.setUser(user);
        m.setContent(output);
        m.setSender(Sender.BOT);

        return messageRepository.save(m);
    }

    @Override
    public FeedbackResponseDTO feedback(Long id, FeedbackRequestDTO request, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        if (!messageRepository.existsByIdAndUserAndSender(id, user, Sender.BOT)) {
            throw new ForbiddenException("Bạn không có quyền đánh giá tin nhắn này!");
        }
        if (feedbackService.exists(TargetType.MESSAGE, id, principal)) {
            throw new ForbiddenException("Bạn đã đánh giá tin nhắn này rồi!");
        }

        return this.feedbackService.create(TargetType.MESSAGE, id, request, principal);
    }

}
