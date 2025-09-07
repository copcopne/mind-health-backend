package com.si.mindhealth.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.si.mindhealth.entities.EmailOTP;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.OTPType;

import jakarta.transaction.Transactional;

public interface EmailOTPRepository extends JpaRepository<EmailOTP, Long> {

    Optional<EmailOTP> findTopByUserAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(
            User user, OTPType type);

    // Rate limit theo user 
    long countByUserAndCreatedAtAfter(User user, Instant after);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM email_otps
        WHERE (expires_at <= ?1 OR consumed_at IS NOT NULL OR attempts >= max_attempts)
        LIMIT ?2
        """, nativeQuery = true)
    int deleteGarbageBatchMysql(Instant now, int batchSize);

    void deleteByUser(User user);
}