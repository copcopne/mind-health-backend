package com.si.mindhealth.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class RefreshRequestDTO {

    @JsonProperty("refresh_token")
    @NotBlank(message = "Refresh Token là bắt buộc!")
    private String refreshToken;
}
