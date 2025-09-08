package com.si.mindhealth.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserStatsController {
    
    @GetMapping("/users/stats")
    public String getUserStats(Principal principal) {
        return "userStats";
    }
}
