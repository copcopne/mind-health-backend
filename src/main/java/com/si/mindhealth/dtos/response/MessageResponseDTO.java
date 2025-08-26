package com.si.mindhealth.dtos.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.Message;
import com.si.mindhealth.entities.enums.Sender;

import lombok.Data;

@Data
public class MessageResponseDTO {

    private Long id;

    private Sender sender;

    private String content;

    @JsonProperty(value = "created_at")
    private Instant createdAt;

    public MessageResponseDTO(Message message) {
        this.id = message.getId();
        this.sender = message.getSender();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }
}
