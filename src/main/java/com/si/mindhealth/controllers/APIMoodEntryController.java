package com.si.mindhealth.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.si.mindhealth.dtos.request.FeedbackRequestDTO;
import com.si.mindhealth.dtos.request.MoodEntryRequestDTO;
import com.si.mindhealth.services.MoodEntryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class APIMoodEntryController {
    private final MoodEntryService moodEntryService;

    @PostMapping(path = "/mood-entries")
    public ResponseEntity<?> create(@Valid @RequestBody MoodEntryRequestDTO request, Principal principal) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(moodEntryService.create(request, principal));
    }

    @GetMapping(path = "/mood-entries")
    public ResponseEntity<?> getList(@RequestParam Map<String, String> params, Principal principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(moodEntryService.getList(params, principal));
    }

    @GetMapping(path = "/mood-entries/{id}")
    public ResponseEntity<?> get(@PathVariable Long id, Principal principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(moodEntryService.get(id, principal));
    }

    @PostMapping(path = "/mood-entries/{id}/feedback")
    public ResponseEntity<?> feedback(@PathVariable Long id, @Valid FeedbackRequestDTO request, Principal principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(moodEntryService.feedback(id, request, principal));
    }
}
