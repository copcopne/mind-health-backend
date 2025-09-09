package com.si.mindhealth.services;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.RegisterRequestDTO;
import com.si.mindhealth.dtos.request.UserRequestDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.dtos.response.UserResponseDTO;
import com.si.mindhealth.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.Map;

public interface UserService extends UserDetailsService {

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getVerifiedUserByUsername(String username);

    User getUserByEmail(String email);

    String getRoleByUsername(String username);

    PageResponseDTO<User> getList(Map<String, String> params);

    boolean authenticate(LoginRequestDTO request);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserResponseDTO updateUser(UserRequestDTO request, Principal principal);

    void updateUser(User user);

    void deleteUserById(Long id);

    UserResponseDTO addUser(RegisterRequestDTO request);
    
    UserResponseDTO addUser(User request);

    UserResponseDTO getProfile(Principal principal);

    UserResponseDTO verifyUser(User user);

    void resetPassword(User user, String rawNewPassword);
}
