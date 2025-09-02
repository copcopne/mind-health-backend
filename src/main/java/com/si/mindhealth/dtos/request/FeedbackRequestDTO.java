package com.si.mindhealth.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.enums.SatisfyLevel;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class FeedbackRequestDTO {

    @JsonProperty(value = "satisfy_level")
    @NotNull(message = "Mức độ hài lòng là bắt buộc!")
    private SatisfyLevel satisfyLevel;  

    private String content;
}
