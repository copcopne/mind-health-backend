package com.si.mindhealth.dtos.response;

import lombok.Data;

@Data
public class UserStatsResponseDTO {
    private String label;
    private Long count;
    private Long total;

    public UserStatsResponseDTO(String label, Long count, Long total) {
        this.label = label;
        this.count = count;
        this.total = total;
    }
}
