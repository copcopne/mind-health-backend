package com.si.mindhealth.services;

import java.security.Principal;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.GetFeedbackRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;

public interface FeedbackService {
    FeedbackResponseDTO create(TargetType targetType, Long targetId, FeedbackRequestDTO request, Principal principal);
    Feedback get(TargetType targetType, Long targetId, Principal principal);
    Feedback get(Long FeedbackId, Principal principal);
    Feedback get(TargetType targetType, Long targetId, User user);
    FeedbackResponseDTO get(GetFeedbackRequestDTO request, Principal principal);
    boolean exists(TargetType targetType, Long targetId, Principal principal);
    boolean exists(MoodEntry entry, Principal principal);
    void delete(TargetType targetType, Long targetId, Principal principal);
    void delete(Long id, Principal principal);
    void delete(TargetType targetType, Long targetId, User user);
}
