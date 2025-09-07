package com.si.mindhealth.controllers;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.OTPRequestDTO;
import com.si.mindhealth.dtos.request.RefreshRequestDTO;
import com.si.mindhealth.dtos.request.ResetPasswordByOTPRequestDTO;
import com.si.mindhealth.dtos.request.VerifyUserByOTPRequestDTO;
import com.si.mindhealth.entities.enums.OTPType;
import com.si.mindhealth.services.AuthService;
import com.si.mindhealth.services.OTPService;

import jakarta.validation.Valid;
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
    private final OTPService otpService;

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) throws Exception {
        return ResponseEntity
                .ok()
                .body(authService.loginHandler(request));
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        return ResponseEntity
                .ok()
                .body(authService.refreshHandler(request));
    }

    @PostMapping(path = "/email-verify")
    public ResponseEntity<?> emailVerify(@Valid @RequestBody OTPRequestDTO request) {
        otpService.sendOTP(request.getEmail(), OTPType.VERIFY);
        return ResponseEntity
                .ok().build();
    }

    @PostMapping(path = "/email-verify/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyUserByOTPRequestDTO request) {
        otpService.verify(request, OTPType.VERIFY);
        return ResponseEntity
                .ok().build();
    }

    @PostMapping(path = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody OTPRequestDTO request) {
        otpService.sendOTP(request.getEmail(), OTPType.RESET_PASSWORD);
        return ResponseEntity
                .ok().build();
    }

    @PostMapping(path = "/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordByOTPRequestDTO request) {
        otpService.verify(request, OTPType.RESET_PASSWORD);
        return ResponseEntity
                .ok().build();
    }
}
