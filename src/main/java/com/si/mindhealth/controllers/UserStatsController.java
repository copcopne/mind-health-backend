package com.si.mindhealth.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.si.mindhealth.dtos.response.UserStatsResponseDTO;
import com.si.mindhealth.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserStatsController {
    private final UserService userService;
    
    @GetMapping("/users/stats")
    public String getUserStats(Model model, Principal principal, @RequestParam Map<String, String> params) {
        UserStatsResponseDTO response = userService.aggregateByTime(params);
        model.addAttribute("data", response);
        return "userStats";
    }
}
