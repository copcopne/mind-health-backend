package com.si.mindhealth.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.GetFeedbackRequestDTO;
import com.si.mindhealth.services.FeedbackService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class APIFeedbackController {
    private final FeedbackService feedbackService;

    @GetMapping("/feedbacks")
    public ResponseEntity<?> getFeedback(
            @RequestParam("target_type") String targetType,
            @RequestParam("target_id") Long targetId,
            Principal principal) {
        GetFeedbackRequestDTO request = new GetFeedbackRequestDTO(targetType, targetId);
        return ResponseEntity.ok(feedbackService.get(request, principal));
    }

    @PutMapping("/feedbacks/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequestDTO request, Principal principal) {
        return ResponseEntity.ok(feedbackService.update(id, request, principal));
    }

    @DeleteMapping("/feedbacks/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal) {
        feedbackService.delete(id, principal);
        return ResponseEntity
                    .noContent()
                    .build();
    }
}
