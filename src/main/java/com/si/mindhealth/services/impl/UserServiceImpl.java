package com.si.mindhealth.services.impl;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.UserRequestDTO;
import com.si.mindhealth.dtos.response.UserResponseDTO;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.exceptions.AuthException;
import com.si.mindhealth.exceptions.InvalidOldPasswordException;
import com.si.mindhealth.exceptions.MyBadRequestException;
import com.si.mindhealth.repositories.UserRepository;
import com.si.mindhealth.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Service("userDetailsService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public User getUserByEmail(String email) {
        Optional<User> u = userRepository.findByEmail(email);
        return u.get();
    }

    @Override
    @Transactional
    public UserResponseDTO addUser(Map<String, String> params) {

        if (userRepository.existsByEmail(params.get("email"))) {
            throw new MyBadRequestException("Email đã tồn tại!");
        }
        Optional<User> user = userRepository.findByUsername(params.get("username"));
        if (user.isPresent()) {
            throw new MyBadRequestException("Tên người dùng đã tồn tại!");
        }
        User u = new User();
        u.setFirstName(params.get("first_name"));
        u.setLastName(params.get("last_name"));
        u.setEmail(params.get("email"));
        u.setUsername(params.get("username"));
        u.setGender(Boolean.valueOf(params.get("gender")));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setRole("ROLE_USER");
        u.setIsActive(true);
        
        return new UserResponseDTO(this.userRepository.save(u));
    }

    @Override
    public String getRoleByUsername(String username) {
        User user = this.getUserByUsername(username);
        return user.getRole();
    }

    @Override
    public boolean authenticate(LoginRequestDTO request) {
        User user = this.getUserByUsername(request.getUsername());
        if (user == null)
            return false;
        return (passwordEncoder.matches(request.getPassword(), user.getPassword()));
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(UserRequestDTO user, Principal principal) {

        User u = this.getUserByUsername(principal.getName());
        if (u == null)
            throw new AuthException("Phiên đăng nhập không hợp lệ!");

        if (user.getPassword() != null)
            if (!this.authenticate(new LoginRequestDTO(u.getUsername(), user.getOldPassword())))
                throw new InvalidOldPasswordException();
            else
            u.setPassword(this.passwordEncoder.encode(user.getPassword()));

        if (user.getGender() != null)
            u.setGender(user.getGender());

        if (user.getFirstName() != null)
            u.setFirstName(user.getFirstName());
            
        if (user.getLastName() != null)
            u.setLastName(user.getLastName());

        userRepository.save(u);

        return new UserResponseDTO(u);

    }

    @Override
    public UserResponseDTO getProfile(Principal principal) {
        User user = this.getUserByUsername(principal.getName());
        return new UserResponseDTO(user);
    }
}
