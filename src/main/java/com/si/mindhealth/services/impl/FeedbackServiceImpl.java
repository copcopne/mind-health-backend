package com.si.mindhealth.services.impl;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.GetFeedbackRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.exceptions.ForbiddenException;
import com.si.mindhealth.exceptions.MyBadRequestException;
import com.si.mindhealth.repositories.FeedbackRepository;
import com.si.mindhealth.repositories.MoodEntryRepository;
import com.si.mindhealth.services.FeedbackService;
import com.si.mindhealth.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final UserService userService;

    @Override
    public FeedbackResponseDTO create(TargetType targetType, Long targetId, FeedbackRequestDTO request,
            Principal principal) {
        boolean isExists = this.exists(targetType, targetId, principal);
        if (isExists == true)
            throw new ForbiddenException("Bạn đã phản hồi đánh giá mục này rồi!");

        Feedback feedback = new Feedback();
        feedback.setUser(userService.getVerifiedUserByUsername(principal.getName()));
        feedback.setTargetType(targetType);
        feedback.setTargetId(targetId);
        feedback.setSatisfyLevel(request.getSatisfyLevel());
        feedback.setContent(request.getContent());

        Feedback saved = feedbackRepository.save(feedback);
        FeedbackResponseDTO response = new FeedbackResponseDTO(saved);
        return response;
    }

    @Override
    public Feedback get(TargetType targetType, Long targetId, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        Optional<Feedback> optional = feedbackRepository.findByUserAndTargetTypeAndTargetId(user, targetType, targetId);
        if (optional.isEmpty())
            return null;
        return optional.get();
    }

    @Override
    public Feedback get(TargetType targetType, Long targetId, User user) {
        Optional<Feedback> optional = feedbackRepository.findByUserAndTargetTypeAndTargetId(user, targetType, targetId);
        if (optional.isEmpty())
            return null;
        return optional.get();
    }

    @Override
    public Feedback get(Long FeedbackId, Principal principal) {
        Optional<Feedback> optional = feedbackRepository.findById(FeedbackId);
        if (optional.isEmpty())
            return null;
        return optional.get();
    }

    @Override
    public boolean exists(TargetType targetType, Long targetId, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());
        boolean isExists = feedbackRepository.existsByUserAndTargetTypeAndTargetId(user, targetType, targetId);
        return isExists;
    }

    @Override
    public void delete(TargetType targetType, Long targetId, Principal principal) {
        User u = userService.getVerifiedUserByUsername(principal.getName());
        this.delete(targetType, targetId, u);
    }

    @Override
    public void delete(TargetType targetType, Long targetId, User user) {
        Feedback feedback = this.get(targetType, targetId, user);
        if (feedback == null) {
            throw new MyBadRequestException("Không tìm thấy đánh giá");
        }
        feedbackRepository.delete(feedback);
    }

    @Override
    public boolean exists(MoodEntry entry, Principal principal) {
        return this.exists(TargetType.MOOD_ENTRY, entry.getId(), principal);
    }

    @Override
    public FeedbackResponseDTO get(GetFeedbackRequestDTO request, Principal principal) {
        if (request.getTargetType() == TargetType.MOOD_ENTRY) {
            Optional<MoodEntry> entry = moodEntryRepository.findByIdAndUser_Username(request.getTargetId(),
                    principal.getName());
            if (entry.isEmpty())
                throw new MyBadRequestException("Không tìm thấy đánh giá!");
        }

        Feedback feedback = this.get(request.getTargetType(), request.getTargetId(), principal);
        if (feedback == null)
            throw new MyBadRequestException("Không tìm thấy đánh giá!");

        FeedbackResponseDTO responseDTO = new FeedbackResponseDTO(feedback);
        return responseDTO;
    }

    @Override
    public void delete(Long id, Principal principal) {
        Feedback feedback = this.get(id, principal);
        if (feedback == null)
            throw new MyBadRequestException("Không tìm thấy phản hồi!");
        feedbackRepository.delete(feedback);
    }

    @Override
    public FeedbackResponseDTO update(Long id, FeedbackRequestDTO request, Principal principal) {
        Feedback feedback = this.get(id, principal);
        if (feedback == null)
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa đánh giá này!");

        feedback.setContent(request.getContent());
        feedback.setSatisfyLevel(request.getSatisfyLevel());
        FeedbackResponseDTO response = new FeedbackResponseDTO(feedbackRepository.save(feedback));
        return response;
    }

    @Override
    public PageResponseDTO<Feedback> getList(Map<String, String> params) {
        int page = NumberUtils.toInt(params.get("page"), 0);
        int size = NumberUtils.toInt(params.get("size"), 10);
        size = Math.min(Math.max(size, 1), 100);

        String sortBy = params.getOrDefault("sort", "createdAt");
        String order = params.getOrDefault("order", "DESC");
        Sort.Direction dir = "ASC".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        String type = params.getOrDefault("type", null);
        TargetType tType = null;
        if (type != null) {
            try {
                tType = TargetType.valueOf(type);
            } catch (Exception ex) {

            }
        }
        String kw = params.getOrDefault("keyword", "");

        Page<Feedback> pageData = feedbackRepository.search(kw, tType, pageable);

        return new PageResponseDTO<>(pageData);
    }

    @Override
    public Feedback get(Long id) {
        Optional<Feedback> optional = feedbackRepository.findById(id);
        if (optional.isEmpty())
            throw new MyBadRequestException("Không tìm thấy đánh giá với id:" + id);
        return optional.get();
    }

    @Override
    public void markedAsRead(Long id) {
        Feedback f = this.get(id);
        f.setRead(true);
        feedbackRepository.save(f);
    }
}
