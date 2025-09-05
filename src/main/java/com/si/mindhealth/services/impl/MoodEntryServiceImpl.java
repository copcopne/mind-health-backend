package com.si.mindhealth.services.impl;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MoodEntryRequestDTO;
import com.si.mindhealth.dtos.response.FeedbackResponseDTO;
import com.si.mindhealth.dtos.response.MoodEntryDetailResponseDTO;
import com.si.mindhealth.dtos.response.MoodEntryResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.exceptions.ForbiddenException;
import com.si.mindhealth.exceptions.MyBadRequestException;
import com.si.mindhealth.exceptions.NotFoundException;
import com.si.mindhealth.repositories.MoodEntryRepository;
import com.si.mindhealth.services.FeedbackService;
import com.si.mindhealth.services.MoodEntryService;
import com.si.mindhealth.services.MoodResultService;
import com.si.mindhealth.services.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoodEntryServiceImpl implements MoodEntryService {
    private final MoodEntryRepository moodEntryRepository;
    private final UserService userService;
    private final FeedbackService feedbackService;
    private final MoodResultService moodResultService;

    private static final long MAX_EDIT_HOURS = 48L;

    @Override
    public MoodEntryResponseDTO create(MoodEntryRequestDTO request, Principal principal) {
        if (request.getMoodLevel() == null)
            throw new MyBadRequestException("Bạn chưa phân loại cảm xúc!");
        if (request.getContent() == null || request.getContent().isBlank())
            throw new MyBadRequestException("Bạn chưa nhập tâm trạng của mình!");

        User user = userService.getVerifiedUserByUsername(principal.getName());
        MoodEntry newEntry = new MoodEntry();

        newEntry.setUser(user);
        newEntry.setContent(request.getContent());
        newEntry.setMoodLevel(request.getMoodLevel());

        MoodEntry entry = moodEntryRepository.save(newEntry);
        moodResultService.CalculateResult(entry, user);
        MoodEntryResponseDTO response = new MoodEntryResponseDTO(entry);

        return response;
    }

    @Override
    public MoodEntryDetailResponseDTO get(Long id, Principal principal) {
        MoodEntry m = this.getMood(id, principal);
        if (m == null)
            throw new NotFoundException("Không tìm thấy nhật ký này của bạn");

        MoodResult result = m.getMoodResult();
        boolean canFeedback = !feedbackService.exists(TargetType.MOOD_ENTRY, id, principal);
        boolean canEdit = this.canEdit(m) && canFeedback;
        MoodEntryDetailResponseDTO response = new MoodEntryDetailResponseDTO(m, result, canEdit, canFeedback);
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

        // map với cả result nếu có
        Page<MoodEntryResponseDTO> dtoPage = pageData.map(entry -> {
            MoodResult result = entry.getMoodResult();
            boolean isEditable = this.canFeedback(entry, principal);
            return new MoodEntryResponseDTO(entry, result, isEditable);
        });

        return new PageResponseDTO<>(dtoPage);
    }

    @Override
    public FeedbackResponseDTO feedback(Long id, FeedbackRequestDTO request, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        if (!moodEntryRepository.existsByIdAndUser(id, user)) {
            throw new ForbiddenException("Bạn không có quyền đánh giá phản hồi này!");
        }
        if (feedbackService.exists(TargetType.MOOD_ENTRY, id, principal)) {
            throw new ForbiddenException("Bạn đã đánh giá phản hồi này rồi!");
        }

        return this.feedbackService.create(TargetType.MOOD_ENTRY, id, request, principal);
    }

    private boolean canEdit(MoodEntry entry) {
        var createdAt = entry.getCreatedAt();
        var now = Instant.now();
        long hours = Duration.between(createdAt, now).toHours();
        return (hours <= MAX_EDIT_HOURS);
    }

    private boolean canFeedback(MoodEntry entry, Principal principal) {
        return !feedbackService.exists(entry, principal);
    }

    @Override
    @Transactional
    public MoodEntryDetailResponseDTO update(Long moodEntryId, MoodEntryRequestDTO request, Principal principal) {
        MoodEntry entry = this.getMood(moodEntryId, principal);
        if (entry == null) {
            throw new NotFoundException("Không tìm thấy nhật ký này của bạn");
        }

        // 1) Chặn nếu đã có feedback
        boolean hasFeedback = feedbackService.exists(TargetType.MOOD_ENTRY, moodEntryId, principal);
        if (hasFeedback) {
            throw new ForbiddenException("Nhật ký này đã có feedback nên không thể chỉnh sửa. Vui lòng tạo bản mới.");
        }

        // 2) Chặn nếu quá thời gian cho phép
        boolean canEdit = canEdit(entry);
        if (!canEdit) {
            throw new ForbiddenException(
                    "Đã quá thời gian cho phép chỉnh sửa (" + MAX_EDIT_HOURS + " giờ). Vui lòng tạo bản mới.");
        }

        // 3) Áp dụng thay đổi (nếu có)
        boolean changed = false;
        boolean changedContent = false;
        if (request.getContent() != null && !request.getContent().equals(entry.getContent())) {
            entry.setContent(request.getContent());
            changed = true;
            changedContent = true;
        }
        if (request.getMoodLevel() != null && request.getMoodLevel() != entry.getMoodLevel()) {
            entry.setMoodLevel(request.getMoodLevel());
            changed = true;
        }
        boolean canFeedback = this.canFeedback(entry, principal);
        if (!changed) {
            // Không đổi gì -> trả về trạng thái hiện tại
            boolean isEditable = true; // chưa có feedback & còn trong thời gian => vẫn sửa tiếp được
            return new MoodEntryDetailResponseDTO(entry, entry.getMoodResult(), isEditable, canFeedback);
        }

        // 4) Lưu & reprocess NLP nếu có cập nhật content
        moodEntryRepository.save(entry);
        boolean isEditable = true;
        User user = userService.getVerifiedUserByUsername(principal.getName());
        if (changedContent) {
            moodResultService.CalculateResult(entry, user);
            return new MoodEntryDetailResponseDTO(entry, null, isEditable, canFeedback);
        }
        // Không cập nhật content -> Lấy kết quả cũ
        return new MoodEntryDetailResponseDTO(entry, entry.getMoodResult(), isEditable, canFeedback);
    }

    @Override
    public void delete(Long moodEntryId, Principal principal) {
        MoodEntry entry = this.getMood(moodEntryId, principal);
        if (entry == null)
            throw new NotFoundException("Không tìm thấy nhật ký này của bạn");

        moodEntryRepository.delete(entry);
        User u = userService.getVerifiedUserByUsername(principal.getName());

        if (u.getIsAcceptSharingData() == false)
            feedbackService.delete(TargetType.MOOD_ENTRY, moodEntryId, u);
    }

    @Override
    public MoodEntry getMood(Long id, Principal principal) {
        User user = userService.getVerifiedUserByUsername(principal.getName());

        Optional<MoodEntry> optional = moodEntryRepository.findByIdAndUser(id, user);

        if (optional.isEmpty())
            return null;

        return optional.get();
    }

    @Override
    public MoodEntry getLastest(User user) {
        Optional<MoodEntry> entry = moodEntryRepository.findTopByUserOrderByCreatedAtDesc(user);
        if (entry.isEmpty())
            return null;
        return entry.get();
    }
}