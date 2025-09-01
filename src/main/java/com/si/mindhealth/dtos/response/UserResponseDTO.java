package com.si.mindhealth.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.User;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;

    private String username;

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

    @JsonProperty(value = "accept_sharing_data")
    private Boolean isAcceptSharingData;

    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return email;

        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];

        // giữ lại 2 ký tự đầu và 1 ký tự cuối
        int start = Math.min(2, name.length()); // tối đa 2 ký tự đầu
        int end = Math.max(1, name.length() - 1); // ít nhất 1 ký tự cuối

        if (name.length() <= 3) {
            // nếu quá ngắn thì không cần che
            return name + "@" + domain;
        }

        String visibleStart = name.substring(0, start);
        String visibleEnd = name.substring(end);
        String maskedMid = "*".repeat(end - start);

        return visibleStart + maskedMid + visibleEnd + "@" + domain;
    }

    public UserResponseDTO(User user) {
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setGender(user.getGender());
        this.setId(user.getId());
        this.setRole(user.getRole());
        this.setIsActive(user.getIsActive());
        this.setIsVerified(user.getIsVerified());
        this.setUsername(user.getUsername());
        this.setEmail(maskEmail(user.getEmail()));
        this.setIsAcceptSharingData(user.getIsAcceptSharingData());
    }

}
