package com.si.mindhealth.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class MessageRequestDTO {

    @NotBlank(message = "Nội dung tin nhắn là bắt buộc!")
    private String content;
}
