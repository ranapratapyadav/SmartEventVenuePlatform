package com.smartevent.user.dto;

import java.time.LocalDateTime;

public class UserResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;

    public UserResponseDto() {
    }

    public UserResponseDto(
            Long userId,
            String username,
            String email,
            String role,
            String status,
            LocalDateTime createdAt) {

        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}