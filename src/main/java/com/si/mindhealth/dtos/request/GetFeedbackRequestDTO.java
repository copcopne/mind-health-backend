package com.si.mindhealth.dtos.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.enums.TargetType;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class GetFeedbackRequestDTO {

    @NotNull(message = "Target type là bắt buộc!")
    @JsonProperty(value = "target_type")
    private TargetType targetType;

    @NotNull(message = "Target id là bắt buộc!")
    @JsonProperty(value = "target_id")
    private Long targetId;

    public GetFeedbackRequestDTO(String targetType, Long targetId) {
        this.targetId = targetId;
        this.targetType = TargetType.valueOf(targetType);
    }
}
