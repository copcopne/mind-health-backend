package com.si.mindhealth.controllers;

import com.si.mindhealth.dtos.request.RegisterRequestDTO;
import com.si.mindhealth.dtos.request.UserRequestDTO;
import com.si.mindhealth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class APIUserController {
    private final UserService userService;

    @PostMapping(path = "/users")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.addUser(request));
    }

    @PatchMapping(path = "/users/profile")
    public ResponseEntity<?> update(@Valid @RequestBody UserRequestDTO user, Principal principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUser(user, principal));
    }

    @GetMapping(path = "/users/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getProfile(principal));
    }
}
