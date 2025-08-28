package com.si.mindhealth.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.enums.MoodLevel;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class MoodEntryRequestDTO {

    @JsonProperty(value = "mood_level")
    private MoodLevel moodLevel;

    @NotBlank(message = "Bạn chưa nhập tâm sự của mình!")
    private String content;

}
