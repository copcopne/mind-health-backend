package com.si.mindhealth.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class RefreshRequestDTO {

    @JsonProperty("refresh_token")
    private String refreshToken;
}
