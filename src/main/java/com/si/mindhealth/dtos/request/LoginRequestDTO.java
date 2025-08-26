package com.si.mindhealth.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class LoginRequestDTO {

    @NotBlank(message = "Tên người dùng là bắt buộc!")
    private String username;

    @NotNull(message = "Mật khẩu là bắt buộc!")
    private String password;
}
