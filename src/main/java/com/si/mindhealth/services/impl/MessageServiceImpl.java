package com.si.mindhealth.services.impl;

import java.security.Principal;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.MessageRequestDTO;
import com.si.mindhealth.dtos.response.MessageResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.entities.Message;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.Sender;
import com.si.mindhealth.repositories.MessageRepository;
import com.si.mindhealth.services.MessageService;
import com.si.mindhealth.services.UserService;
import com.si.mindhealth.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final UserService userService;
    private final MessageRepository messageRepository;

    // User luu tin nhan -> chatbot phan hoi,
    // User vao muc nhan tin chatbot -> chatbot tao 1 cau mo dau

    @Override
    public MessageResponseDTO create(MessageRequestDTO request, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        Message message = new Message();
        message.setUser(user);
        message.setContent(request.getContent());
        message.setSender(Sender.USER);

        Message saved = messageRepository.save(message);
        
        MessageResponseDTO response = new MessageResponseDTO(saved);
        return response;
    }

    @Override
    public PageResponseDTO<MessageResponseDTO> getList(Map<String, String> params, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        int page = NumberUtils.toInt(params.get("page"), 0);
        int size = NumberUtils.toInt(params.get("size"), 10);

        size = Math.min(Math.max(size, 1), 100);

        String sortBy = params.getOrDefault("sort", "createdAt");
        String order  = params.getOrDefault("order", "DESC");
        Sort.Direction dir = "ASC".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));

        Page<Message> pageData = messageRepository.findByUser(user, pageable);


        PageResponseDTO<MessageResponseDTO> response = new PageResponseDTO<>(
            pageData.map(MessageResponseDTO::new)
        );

        return response;
    }

    @Override
    public Message getResponse(Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        Message lastMessage = messageRepository
            .findFirstByOrderByCreatedAtDesc()
            .orElse(null);

        Message lastUserMessage = messageRepository
            .findFirstByUserAndSenderOrderByCreatedAtDesc(user, Sender.USER)
            .orElse(null);

        Message lastBotMessage = messageRepository
            .findFirstByUserAndSenderOrderByCreatedAtDesc(user, Sender.BOT)
            .orElse(null);

        if (
            (lastUserMessage == null && lastBotMessage == null) || // Lầu đầu vào nhắn
            (TimeUtils.isToday(lastBotMessage.getCreatedAt())) // Bot hôm nay chưa nhắn câu mở chào
        ) {
            /// Xử lý câu mở chào
        }
        if (lastMessage != null) {
            // Xử lý trả phản hồi
        }
    
        return null;
    }
    
}
