package com.si.mindhealth.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.si.mindhealth.entities.EmailOTP;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.OTPType;

public interface EmailOTPRepository extends JpaRepository<EmailOTP, Long> {

    Optional<EmailOTP> findTopByUserAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(
            User user, OTPType type);

    // Rate limit theo user 
    long countByUserAndCreatedAtAfter(User user, Instant after);

    @Modifying
    @Query("delete from EmailOTP o where o.user = :user and o.type = :type and o.consumedAt is null")
    int deleteAllActiveByUserAndType(@Param("user") User user, @Param("type") OTPType type);

    void deleteByUser(User user);
}