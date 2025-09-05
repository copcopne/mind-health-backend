package com.si.mindhealth.services;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MoodEntryRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.dtos.response.MoodEntryDetailResponseDTO;
import com.si.mindhealth.dtos.response.MoodEntryResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.dtos.response.StatsResponseDTO;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;

public interface MoodEntryService {
    MoodEntryResponseDTO create(MoodEntryRequestDTO request, Principal principal);

    MoodEntryDetailResponseDTO update(Long moodEntryId, MoodEntryRequestDTO request, Principal principal);

    void delete(Long moodEntryId, Principal principal);

    MoodEntryDetailResponseDTO get(Long id, Principal principal);

    MoodEntry getMood(Long id, Principal principal);
    
    MoodEntry getLastest(User user);

    PageResponseDTO<MoodEntryResponseDTO> getList(Map<String, String> parmas, Principal principal);

    FeedbackResponseDTO feedback(Long id, FeedbackRequestDTO request, Principal principal);

    List<StatsResponseDTO> getStats(Map<String, String> params, Principal principal);
}
