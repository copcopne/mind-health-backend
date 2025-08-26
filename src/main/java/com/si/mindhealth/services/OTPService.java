package com.si.mindhealth.services;

import com.si.mindhealth.dtos.request.ResetPasswordByOTPRequestDTO;
import com.si.mindhealth.dtos.request.VerifyUserByOTPRequestDTO;
import com.si.mindhealth.entities.enums.OTPType;

public interface OTPService {
    void sendOTP(String email, OTPType type);

    void verify(ResetPasswordByOTPRequestDTO request, OTPType type);
    
    void verify(VerifyUserByOTPRequestDTO request, OTPType type);
}
