package com.si.mindhealth.controllers;

import com.si.mindhealth.entities.User;
import com.si.mindhealth.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIUserController {
    private  final UserService userService;

    public APIUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/users",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.userService.addUser(params), HttpStatus.CREATED);
    }
}
