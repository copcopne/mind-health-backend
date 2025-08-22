package com.si.mindhealth.services;

import java.security.Principal;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.entities.enums.TargetType;

public interface FeedbackService {
    FeedbackResponseDTO create(TargetType targetType, Long targetId, FeedbackRequestDTO request, Principal principal);
}
