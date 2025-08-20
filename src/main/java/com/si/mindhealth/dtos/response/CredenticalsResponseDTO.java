package com.si.mindhealth.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CredenticalsResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("expires_in")
    private int expiresIn;

    public CredenticalsResponseDTO(String accesss, String refresh, int expries) {
        this.accessToken = accesss;
        this.refreshToken = refresh;
        this.expiresIn = expries;
    }
}
