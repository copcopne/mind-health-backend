package com.si.mindhealth.services;

import com.si.mindhealth.entities.enums.OTPType;

public interface MailService {
    void sendOTPEmail(String email, String code, OTPType type);
}
