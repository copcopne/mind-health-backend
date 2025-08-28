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

import com.si.mindhealth.dtos.TopicMultiResult;
import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MoodEntryRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.dtos.response.MoodEntryResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.exceptions.ForbiddenException;
import com.si.mindhealth.exceptions.NotFoundException;
import com.si.mindhealth.repositories.MoodEntryRepository;
import com.si.mindhealth.services.FeedbackService;
import com.si.mindhealth.services.MoodEntryService;
import com.si.mindhealth.services.TopicDetector;
import com.si.mindhealth.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoodEntryServiceImpl implements MoodEntryService {
    private final MoodEntryRepository moodEntryRepository;
    private final UserService userService;
    private final FeedbackService feedbackService;
    private final TopicDetector detector;

    @Override
    public MoodEntryResponseDTO create(MoodEntryRequestDTO request, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());
        MoodEntry entry = new MoodEntry();

        entry.setUser(user);
        entry.setContent(request.getContent());
        entry.setMoodLevel(request.getMoodLevel());

        entry.setId(moodEntryRepository.save(entry).getId());

        TopicMultiResult results = detector.detectMulti(request);
        

        MoodEntryResponseDTO response = new MoodEntryResponseDTO(entry);
        return response;
    }

    @Override
    public MoodEntryResponseDTO get(Long id, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        Optional<MoodEntry> optional = moodEntryRepository.findByIdAndUser(id, user);

        if (optional.isEmpty())
            throw new NotFoundException("Không tìm thấy nhật ký này của bạn");

        MoodEntryResponseDTO response = new MoodEntryResponseDTO(optional.get());
        return response;
    }

    @Override
    public PageResponseDTO<MoodEntryResponseDTO> getList(Map<String, String> params, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        int page = NumberUtils.toInt(params.get("page"), 0);
        int size = NumberUtils.toInt(params.get("size"), 10);

        size = Math.min(Math.max(size, 1), 100);

        String sortBy = params.getOrDefault("sort", "createdAt");
        String order = params.getOrDefault("order", "DESC");
        Sort.Direction dir = "ASC".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));

        Page<MoodEntry> pageData = moodEntryRepository.findByUser(user, pageable);
        PageResponseDTO<MoodEntryResponseDTO> response = new PageResponseDTO<>(
                pageData.map(MoodEntryResponseDTO::new));

        return response;
    }

    @Override
    public FeedbackResponseDTO feedback(Long id, FeedbackRequestDTO request, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());
        if (moodEntryRepository.existsByIdAndUser(id, user))
            throw new ForbiddenException("Bạn không có quyền đánh giá phản hồi này!");

        return this.feedbackService.create(TargetType.SUPPORT_RESPONSE, id, request, principal);
    }
}