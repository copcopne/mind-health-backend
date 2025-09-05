package com.si.mindhealth.services;

import java.security.Principal;
import java.util.Map;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MessageRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.dtos.response.MessageResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;

public interface MessageService {
    MessageResponseDTO create(MessageRequestDTO request, Principal principal);

    PageResponseDTO<MessageResponseDTO> getList(Map<String, String> params, Principal principal);

    FeedbackResponseDTO feedback(Long id, FeedbackRequestDTO request, Principal principal);
}
