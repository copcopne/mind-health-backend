package com.si.mindhealth.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.si.mindhealth.services.EmailOTPBatchedCleanupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailOTPCleanupJob {
    private final EmailOTPBatchedCleanupService service;

    // Mỗi 15 phút
    @Scheduled(cron = "0 */15 * * * *")
    public void run() {
        int n = service.cleanupOnce();
        if (n > 0) {
            log.info("Cleaned {} OTP rows.", n);
        }
    }
}