package com.si.mindhealth.services.impl;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.RegisterRequestDTO;
import com.si.mindhealth.dtos.request.UserRequestDTO;
import com.si.mindhealth.dtos.response.UserResponseDTO;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.exceptions.ForbiddenException;
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
        User user = this.getUserByUsername(username);
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isEmpty())
            throw new MyBadRequestException("Không tồn tại người dùng với username: " + username + "!");

        return u.get();
    }

    @Override
    public User getVerifiedUserByUsername(String username) {
        User user = this.getUserByUsername(username);

        if (user.getIsVerified() == false)
            throw new ForbiddenException("Tài khoản chưa được xác minh!");

        if (user.getIsActive() == false)
            throw new ForbiddenException("Tài khoản đã bị khóa!");

        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> u = userRepository.findByEmail(email);
        if (u.isPresent())
            return u.get();
        return null;
    }

    @Override
    @Transactional
    public UserResponseDTO addUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new MyBadRequestException("Email đã tồn tại!");
        }
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isPresent()) {
            throw new MyBadRequestException("Tên người dùng đã tồn tại!");
        }
        User u = new User();
        u.setFirstName(request.getFirstName());
        u.setLastName(request.getLastName());
        u.setEmail(request.getEmail());
        u.setUsername(request.getUsername());
        u.setGender(Boolean.valueOf(request.getGender()));
        u.setPassword(this.passwordEncoder.encode(request.getPassword()));
        u.setRole("ROLE_USER");
        u.setIsActive(true);

        if(request.getAcceptSharingData() == null)
            u.setIsAcceptSharingData(true);
        else u.setIsAcceptSharingData(request.getAcceptSharingData());

        UserResponseDTO newUserDTO = new UserResponseDTO(this.userRepository.save(u));

        return newUserDTO;
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
        User u = this.getVerifiedUserByUsername(principal.getName());

        if (user.getPassword() != null)
            if (!this.authenticate(new LoginRequestDTO(u.getUsername(), user.getOldPassword())))
                throw new MyBadRequestException("Mật khẩu cũ không đúng!");
            else
                u.setPassword(this.passwordEncoder.encode(user.getPassword()));

        if (user.getGender() != null)
            u.setGender(user.getGender());

        if (user.getFirstName() != null)
            u.setFirstName(user.getFirstName());

        if (user.getLastName() != null)
            u.setLastName(user.getLastName());

        if (user.getAcceptSharingData() != null)
            u.setIsAcceptSharingData(user.getAcceptSharingData());

        userRepository.save(u);
        return new UserResponseDTO(u);

    }

    @Override
    public UserResponseDTO getProfile(Principal principal) {
        User user = this.getUserByUsername(principal.getName());
        return new UserResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO verifyUser(User u) {
        
        u.setIsVerified(true);
        User newUser = userRepository.save(u);
        UserResponseDTO response = new UserResponseDTO(newUser);

        return response;
    }

    @Override
    @Transactional
    public void resetPassword(User u, String rawNewPassword) {

        u.setPassword(passwordEncoder.encode(rawNewPassword));
        userRepository.save(u);
    }
}
