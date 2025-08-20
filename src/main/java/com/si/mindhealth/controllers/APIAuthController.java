package com.si.mindhealth.controllers;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.RefreshRequestDTO;
import com.si.mindhealth.services.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class APIAuthController {
    private final AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {

        return ResponseEntity
            .ok()
            .body(authService.loginHandler(request));
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequestDTO request) {

        return ResponseEntity
            .ok()
            .body(authService.refreshHandler(request));
    }
}
