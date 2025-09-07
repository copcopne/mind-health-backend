package com.si.mindhealth.services.impl;

import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.RefreshRequestDTO;
import com.si.mindhealth.dtos.response.CredenticalsResponseDTO;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.exceptions.AuthException;
import com.si.mindhealth.services.AuthService;
import com.si.mindhealth.services.UserService;
import com.si.mindhealth.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;

    @Override
    public CredenticalsResponseDTO loginHandler(LoginRequestDTO request) throws Exception {
        String username = request.getUsername();
        if (this.userService.authenticate(request)) {
                    User u = userService.getUserByUsername(username);
                    String accessToken = JwtUtils.generateAccessToken(username, u.getRole());
                    String refreshToken = JwtUtils.generateRefreshToken(username);
                    
                    return new CredenticalsResponseDTO(accessToken, refreshToken, 15 * 60);
        }
        throw new AuthException();
    }

    @Override
    public CredenticalsResponseDTO refreshHandler(RefreshRequestDTO request) {
        try {
            String username = JwtUtils.getSubject(request.getRefreshToken());
            String role = userService.getRoleByUsername(username);
            String newAccess = JwtUtils.generateAccessToken(username, role);
            String newRefresh = JwtUtils.generateRefreshToken(username);

            return new CredenticalsResponseDTO(newAccess, newRefresh, 15 * 60);
        } catch (Exception e) {
            throw new AuthException("Lỗi xảy ra khi tạo token!");
        }
    }
    
}
