package com.si.mindhealth.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserRequestDTO {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private Boolean gender;

    @JsonProperty("old_password")
    private String oldPassword;

    private String password;

    private String confirm;

    @JsonProperty(value = "accept_sharing_data")
    private Boolean acceptSharingData;
}
