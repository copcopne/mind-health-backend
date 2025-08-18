package com.si.mindhealth.services.impl;

import com.si.mindhealth.entities.User;
import com.si.mindhealth.repositories.UserRepository;
import com.si.mindhealth.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isEmpty())
            throw new UsernameNotFoundException(
                    String.format("Không tìm thấy người dùng với username: %s", username)
            );
        User user = u.get();
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> u = userRepository.findByUsername(username);
        return u.get();
    }

    @Override
    public User addUser(Map<String, String> params) {
        return null;
    }

    @Override
    public String getRoleByUsername(String username) {
        User user = this.getUserByUsername(username);
        return user.getRole();
    }

    @Override
    public boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public User updateUser(Map<String, String> params, User user) {
        return null;
    }
}
