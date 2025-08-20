package com.si.mindhealth.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Tên người dùng không được để trống!")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống!")
    private String password;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống!")
    private String email;

    @JsonProperty("first_name")
    @NotBlank(message = "Tên không được để trống!")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Họ không được để trống!")
    private String lastName;

    @NotBlank(message = "Giới tính không được để trống!")
    private Boolean gender;
}
