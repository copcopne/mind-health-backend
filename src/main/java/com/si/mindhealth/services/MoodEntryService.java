package com.si.mindhealth.services;

import java.security.Principal;
import java.util.Map;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MoodEntryRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.dtos.response.MoodEntryResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;

public interface MoodEntryService {
    MoodEntryResponseDTO create(MoodEntryRequestDTO request, Principal principal);
    MoodEntryResponseDTO get(Long id, Principal principal);
    PageResponseDTO<MoodEntryResponseDTO> getList(Map<String, String> parmas, Principal principal);
    FeedbackResponseDTO feedback(Long id, FeedbackRequestDTO request, Principal principal);
}
