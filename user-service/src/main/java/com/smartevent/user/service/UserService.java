package com.smartevent.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartevent.user.dto.UserResponseDto;
import com.smartevent.user.entity.User;
import com.smartevent.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(
                    "User already exists with email: "
                    + user.getEmail());
        }

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(user.getPassword()));

        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public UserResponseDto getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: "
                                + userId));

        return convertToDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: "
                                + email));

        return convertToDto(user);
    }

    private UserResponseDto convertToDto(User user) {

        return new UserResponseDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}