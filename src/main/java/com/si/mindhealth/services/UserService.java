package com.si.mindhealth.services;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.RegisterRequestDTO;
import com.si.mindhealth.dtos.request.UserRequestDTO;
import com.si.mindhealth.dtos.response.UserResponseDTO;
import com.si.mindhealth.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;

public interface UserService extends UserDetailsService {

    User getUserByUsername(String username);

    User getVerifiedUserByUsername(String username);

    User getUserByEmail(String email);

    String getRoleByUsername(String username);

    boolean authenticate(LoginRequestDTO request);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserResponseDTO updateUser(UserRequestDTO request, Principal principal);

    UserResponseDTO addUser(RegisterRequestDTO request);

    UserResponseDTO getProfile(Principal principal);

    UserResponseDTO verifyUserByEmail(String email);

    void resetPasswordByEmail(String email, String rawNewPassword);
}
