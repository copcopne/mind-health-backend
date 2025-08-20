package com.si.mindhealth.services.impl;

import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.RefreshRequestDTO;
import com.si.mindhealth.dtos.response.CredenticalsResponseDTO;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.exceptions.AuthException;
import com.si.mindhealth.exceptions.MyBadRequestException;
import com.si.mindhealth.services.AuthService;
import com.si.mindhealth.services.UserService;
import com.si.mindhealth.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;

    @Override
    public CredenticalsResponseDTO loginHandler(LoginRequestDTO request){
        if (request.getUsername().isBlank() || request.getPassword().isBlank())
            throw new MyBadRequestException("Tài khoản hoặc mật khẩu không được trống!");
        
                    
        String username = request.getUsername();
        if (this.userService.authenticate(request)) {
                try {
                    User u = userService.getUserByUsername(username);
                    String accessToken = JwtUtils.generateAccessToken(username, u.getRole());
                    String refreshToken = JwtUtils.generateRefreshToken(username);
                    
                    return new CredenticalsResponseDTO(accessToken, refreshToken, 15 * 60);
                }
                catch (Exception ex) {
                    throw new AuthException();
                }
        }
        throw new MyBadRequestException("Tài khoản hoặc mật khẩu không đúng!");
    }

    @Override
    public CredenticalsResponseDTO refreshHandler(RefreshRequestDTO request) {

        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new MyBadRequestException("Refresh token là bắt buộc!");
        }
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
