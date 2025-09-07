package com.si.mindhealth.dtos.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsResponseDTO {
    
    private LocalDate day;
    
    @JsonProperty("mood_values")
    private List<Integer> moodValues;
}
