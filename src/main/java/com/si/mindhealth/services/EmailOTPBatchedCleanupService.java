package com.si.mindhealth.services;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.si.mindhealth.repositories.EmailOTPRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOTPBatchedCleanupService {
    private final EmailOTPRepository repo;

    private static final int BATCH_SIZE = 1000;
    private static final int HARD_CAP  = 50_000;

    @Transactional
    public int cleanupOnce() {
        int total = 0;
        int loop  = 0;
        Instant now = Instant.now();

        while (total < HARD_CAP) {
            int deleted = repo.deleteGarbageBatchMysql(now, BATCH_SIZE);
            if (deleted == 0) break;
            total += deleted;
            loop++;
        }

        log.info("[OTP Cleanup] Deleted {} rows in {} batch(es).", total, loop);
        return total;
    }
}
