package com.si.mindhealth.dtos;

import com.si.mindhealth.dtos.request.ResetPasswordByOTPRequestDTO;
import com.si.mindhealth.dtos.request.VerifyUserByOTPRequestDTO;

import lombok.Data;

@Data
public class VerifyDTO {
    private String code;
    
    private String newPassword;

    private String email;

    public VerifyDTO(VerifyUserByOTPRequestDTO request) {
        this.code = request.getCode();
        this.email = request.getEmail();
    }

    public VerifyDTO(ResetPasswordByOTPRequestDTO request) {
        this.code = request.getCode();
        this.email = request.getEmail();
        this.newPassword = request.getNewPassword();
    }
}
