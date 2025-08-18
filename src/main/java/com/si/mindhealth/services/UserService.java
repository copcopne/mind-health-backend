package com.si.mindhealth.services;

import com.si.mindhealth.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface UserService extends UserDetailsService {
    User getUserByUsername(String username);
    User addUser(Map<String, String> params);
    String getRoleByUsername(String username);
    boolean authenticate(String username, String password);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User updateUser(Map<String, String> params, User user);
}
