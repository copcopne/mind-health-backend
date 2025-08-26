package com.si.mindhealth.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class VerifyUserByOTPRequestDTO {
    
    @Email(message = "Email không hợp lệ!")
    @NotBlank(message = "Email không được để trống!")
    private String email;

    @NotBlank(message = "OTP không được để trống!")
    private String code;
}
