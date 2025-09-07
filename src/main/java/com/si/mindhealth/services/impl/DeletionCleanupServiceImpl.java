package com.si.mindhealth.services.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.si.mindhealth.entities.DeletionRequest;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.DeletionStatus;
import com.si.mindhealth.repositories.*;
import com.si.mindhealth.services.DeletionCleanupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletionCleanupServiceImpl implements DeletionCleanupService {

    private final DeletionRequestRepository deletionRequestRepository;
    private final MessageRepository messageRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final FeedbackRepository feedbackRepository;
    private final EmailOTPRepository emailOtpRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public int processApprovedRequests() {
        List<DeletionRequest> requests = deletionRequestRepository
                .findByStatusAndProcessedAtIsNull(DeletionStatus.APPROVED);

        int done = 0;
        for (DeletionRequest req : requests) {
            try {
                cleanupOne(req);
                done++;
            } catch (Exception ex) {
                log.error("Cleanup failed for DeletionRequest id={} userId={}: {}",
                        req.getId(), req.getUser().getId(), ex.getMessage(), ex);
            }
        }
        return done;
    }

    protected void cleanupOne(DeletionRequest req) {
        if (req.getCreatedAt().isAfter(Instant.now().minus(7, ChronoUnit.DAYS)))
            return;

        User user = req.getUser();

        // Xoá dữ liệu liên quan
        emailOtpRepository.deleteByUser(user);
        messageRepository.deleteByUser(user);
        moodEntryRepository.deleteByUser(user);
        feedbackRepository.deleteByUser(user);

        // Ẩn danh user
        anonymizeUser(user);

        // Đánh dấu đã xử lý
        req.setProcessedAt(Instant.now());
        deletionRequestRepository.save(req);
    }

    private void anonymizeUser(User user) {
        // đổi các trường PII
        String suffix = "deleted-" + user.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String fakeEmail = suffix + "@example.invalid";
        String fakeUsername = suffix;

        user.setEmail(fakeEmail);
        user.setUsername(fakeUsername);
        user.setFirstName("Deleted User");
        user.setLastName("Deleted User");
        user.setIsActive(false);
        user.setIsVerified(false);

        user.setPassword(UUID.randomUUID().toString());

        userRepository.save(user);
    }
}
