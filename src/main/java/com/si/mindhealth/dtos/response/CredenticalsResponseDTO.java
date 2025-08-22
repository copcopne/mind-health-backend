package com.si.mindhealth.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CredenticalsResponseDTO {

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;
    
    @JsonProperty(value = "expires_in")
    private int expiresIn;

    public CredenticalsResponseDTO(String accesss, String refresh, int expries) {
        this.accessToken = accesss;
        this.refreshToken = refresh;
        this.expiresIn = expries;
    }
}
