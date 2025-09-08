package com.si.mindhealth.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Principal principal) {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
