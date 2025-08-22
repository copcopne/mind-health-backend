package com.si.mindhealth.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.enums.SatisfyLevel;
import com.si.mindhealth.entities.enums.TargetType;

import lombok.Data;

@Data
public class FeedbackResponseDTO {

    private Long id;

    @JsonProperty(value = "target_type")
    private TargetType targetType;
    
    @JsonProperty(value = "target_id")
    private Long targetId;

    @JsonProperty(value = "satisfy_level")
    private SatisfyLevel satisfyLevel;

    private String content;

    public FeedbackResponseDTO(Feedback feedback) {
        this.id = feedback.getId();
        this.targetType = feedback.getTargetType();
        this.targetId = feedback.getTargetId();
        this.satisfyLevel = feedback.getSatisfyLevel();
        this.content = feedback.getContent();
    }
}
