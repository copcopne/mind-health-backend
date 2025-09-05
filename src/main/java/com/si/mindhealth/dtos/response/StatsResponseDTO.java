package com.si.mindhealth.dtos.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StatsResponseDTO {
    
    private LocalDate day;

    @JsonProperty(value = "mood_index")
    private Double moodIndex;

    public StatsResponseDTO(LocalDate day, Double moodIndex) {
        this.day = day;
        this.moodIndex = moodIndex;
    }
}
