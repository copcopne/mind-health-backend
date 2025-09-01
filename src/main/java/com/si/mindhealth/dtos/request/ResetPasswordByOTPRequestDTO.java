package com.si.mindhealth.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ResetPasswordByOTPRequestDTO {

    @Email(message = "Email không hợp lệ!")
    @NotBlank(message = "Email không được để trống!")
    private String email;

    @JsonProperty(value = "new_password")
    @NotBlank(message = "Mật khẩu không được để trống!")
    private String newPassword;

    @NotBlank(message = "OTP không được để trống!")
    private String code;
}
