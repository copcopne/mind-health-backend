package com.si.mindhealth.controllers;

import com.si.mindhealth.entities.User;
import com.si.mindhealth.services.UserService;
import com.si.mindhealth.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIAuthController {
    private final UserService userService;

    public APIAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request){
        String username = request.get("username");
        String password = request.get("password");

        if (!this.userService.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Sai thông tin đăng nhập!"));
        }
        if (!this.userService.authenticate(username, password)) {
            try {
                User u = userService.getUserByUsername(username);
                String accessToken = JwtUtils.generateAccessToken(username, u.getRole());
                String refreshToken = JwtUtils.generateRefreshToken(username);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                                "access_token", accessToken,
                                "refresh_token", refreshToken,
                                "expires_in", 15 * 60
                        ));
            } catch (Exception e) {
                return ResponseEntity.status(500)
                        .body(Map.of("message", "Lỗi xảy ra khi đăng nhập!"));
            }
        }
        return ResponseEntity.status(400)
                .body(Map.of("message", "Sai thông tin đăng nhập!"));
    }

    public ResponseEntity<?> refresh(@RequestBody Map<String, String> params){
        String refreshToken = params.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", "Refresh token là bắt buộc!"));
        }
        try {
            String username = JwtUtils.getSubject(refreshToken);
            String role = userService.getRoleByUsername(username);
            String newAccess = JwtUtils.generateAccessToken(username, role);
            String newRefresh = JwtUtils.generateRefreshToken(username);

            return ResponseEntity.status(200)
                    .body(Map.of(
                            "access_token", newAccess,
                                "refresh_token", newRefresh,
                                "expires_in", 15 * 60
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                        .body(Map.of("message", "Lỗi xảy ra khi tạo token!"));
        }
    }
}
