package com.si.mindhealth.services.impl;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.exceptions.ForbiddenException;
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
    public FeedbackResponseDTO create(TargetType targetType, Long targetId, FeedbackRequestDTO request, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());

        if (feedbackRepository.existsByUserAndTargetTypeAndTargetId(user, targetType, targetId))
            throw new ForbiddenException("Bạn đã phản hồi đánh giá mục này rồi!");

        Feedback feedback = new Feedback();
        feedback.setTargetType(targetType);
        feedback.setTargetId(targetId);
        feedback.setSatisfyLevel(request.getSatisfyLevel());
        feedback.setContent(request.getContent());

        Feedback saved = feedbackRepository.save(feedback);
        FeedbackResponseDTO response = new FeedbackResponseDTO(saved);
        return response;
    }
}
