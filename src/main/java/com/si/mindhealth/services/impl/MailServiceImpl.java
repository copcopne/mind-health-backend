package com.si.mindhealth.services.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.si.mindhealth.entities.enums.OTPType;
import com.si.mindhealth.services.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendOTPEmail(String email, String code, OTPType type) {
        var msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject(type == OTPType.VERIFY ? "Mã xác minh email" : "Mã đặt lại mật khẩu");
        msg.setText("Mã OTP của bạn là: " + code + "\nMã sẽ hết hạn sau 10 phút.");
        mailSender.send(msg);
    }
}
