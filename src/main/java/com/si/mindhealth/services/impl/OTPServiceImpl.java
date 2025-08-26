package com.si.mindhealth.services.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.VerifyDTO;
import com.si.mindhealth.dtos.request.ResetPasswordByOTPRequestDTO;
import com.si.mindhealth.dtos.request.VerifyUserByOTPRequestDTO;
import com.si.mindhealth.entities.EmailOTP;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.OTPType;
import com.si.mindhealth.exceptions.MyBadRequestException;
import com.si.mindhealth.repositories.EmailOTPRepository;
import com.si.mindhealth.services.MailService;
import com.si.mindhealth.services.OTPService;
import com.si.mindhealth.services.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final EmailOTPRepository repo;
    private final MailService mailService;
    private final UserService userService;

    private static final Duration EXPIRES_IN = Duration.ofMinutes(10);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_PER_HOUR_PER_EMAIL = 5;

    private String generate6Digits() {
        var rnd = new SecureRandom();
        int n = rnd.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    private String sha256Hex(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : bytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void sendOTP(String email, OTPType type) {
        User user = userService.getUserByEmail(email);
        if (user == null)
            return;

        // rate limit
        var now = Instant.now();
        if (repo.countByUserAndCreatedAtAfter(user, now.minus(Duration.ofHours(1))) >= MAX_PER_HOUR_PER_EMAIL) {
            throw new MyBadRequestException("Quá số lần gửi OTP trong 1 giờ, vui lòng thử lại sau.");
        }

        // chặn spam resend < 60s
        repo.findTopByUserAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(user, type)
                .ifPresent(last -> {
                    if (Duration.between(last.getCreatedAt(), now).compareTo(RESEND_COOLDOWN) < 0) {
                        throw new MyBadRequestException("Gửi hơi nhanh á, chờ 60s rồi gửi lại bạn nha.");
                    }
                });

        var code = generate6Digits();
        var otp = new EmailOTP();
        otp.setUser(user);
        otp.setType(type);
        otp.setCodeHash(sha256Hex(code));
        otp.setCreatedAt(now);
        otp.setExpiresAt(now.plus(EXPIRES_IN));
        otp.setMaxAttempts(5);
        repo.save(otp);

        mailService.sendOTPEmail(email, code, type);
    }
    
    @Transactional
    public void verify(VerifyDTO request, OTPType type) {

        User user = userService.getUserByEmail(request.getEmail());
        if (user == null)
            throw new MyBadRequestException("OTP không đúng.");

        var otp = repo.findTopByUserAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(user, type)
                .orElseThrow(() -> new RuntimeException("OTP không tồn tại hoặc đã dùng."));

        if (Instant.now().isAfter(otp.getExpiresAt()))
            throw new MyBadRequestException("OTP đã hết hạn.");

        if (otp.getAttempts() >= otp.getMaxAttempts())
            throw new MyBadRequestException("Nhập sai quá số lần cho phép.");

        otp.setAttempts(otp.getAttempts() + 1);
        var ok = sha256Hex(request.getCode()).equals(otp.getCodeHash());
        if (!ok) {
            repo.save(otp);
            throw new MyBadRequestException("OTP không đúng.");
        }

        otp.setConsumedAt(Instant.now());
        repo.save(otp);

        if (type == OTPType.RESET_PASSWORD)
            userService.resetPasswordByEmail(user.getEmail(), request.getNewPassword());
    }

    @Override
    public void verify(ResetPasswordByOTPRequestDTO request, OTPType type) {
        VerifyDTO req = new VerifyDTO(request);
        this.verify(req, type);
    }

    @Override
    public void verify(VerifyUserByOTPRequestDTO request, OTPType type) {
        VerifyDTO req = new VerifyDTO(request);
        this.verify(req, type);
    }
}
