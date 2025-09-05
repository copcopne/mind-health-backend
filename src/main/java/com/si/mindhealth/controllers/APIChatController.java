package com.si.mindhealth.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MessageRequestDTO;
import com.si.mindhealth.services.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class APIChatController {
    private final MessageService messageService;


    @GetMapping(path = "/messages")
    public ResponseEntity<?> getList(@RequestParam Map<String, String> params, Principal principal) {
        return ResponseEntity
                .ok()
                .body(messageService.getList(params, principal));
    }

    @PostMapping(path = "/messages")
    public ResponseEntity<?> sentMessage(@Valid @RequestBody MessageRequestDTO request, Principal principal) {
        return ResponseEntity
                .ok()
                .body(messageService.create(request, principal));
    }

    @PostMapping(path = "/messages/{id}/feedback")
    public ResponseEntity<?> feedbackChatBotMessage(@PathVariable Long id, @Valid @RequestBody FeedbackRequestDTO request, Principal principal) {
        return ResponseEntity
                .ok()
                .body(messageService.feedback(id, request, principal));
    }
    
}
