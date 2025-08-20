package com.si.mindhealth.dtos.request;

import lombok.Value;

@Value
public class LoginRequestDTO {

    private String username;
    
    private String password;
}
