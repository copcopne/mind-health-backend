package com.si.mindhealth.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.si.mindhealth.services.DeletionCleanupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeletionCleanupJob {
    private final DeletionCleanupService deletionCleanupService;

    /**
     * Chạy lúc 03:00 mỗi ngày theo server time (nên cấu hình TZ = Asia/Ho_Chi_Minh).
     * Cron: second minute hour day-of-month month day-of-week
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void runDaily() {
        int count = deletionCleanupService.processApprovedRequests();
        log.info("DeletionCleanupJob done: processed {} approved requests", count);
    }
}
