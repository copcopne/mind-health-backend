package com.si.mindhealth.services.impl;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.exceptions.ForbiddenException;
import com.si.mindhealth.exceptions.MyBadRequestException;
import com.si.mindhealth.repositories.FeedbackRepository;
import com.si.mindhealth.services.FeedbackService;
import com.si.mindhealth.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
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
}
