package com.si.mindhealth.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.User;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;

    private String email;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    private Boolean gender;

    private String role;

    @JsonProperty(value = "is_active")
    private Boolean isActive;

    @JsonProperty(value = "is_verified")
    private Boolean isVerified;

    public UserResponseDTO(User user) {
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setGender(user.getGender());
        this.setId(user.getId());
        this.setRole(user.getRole());
        this.setIsActive(user.getIsActive());
        this.setIsVerified(user.getIsVerified());
        this.setEmail(user.getEmail());
    }
}
