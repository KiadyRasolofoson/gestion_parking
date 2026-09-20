package com.example.parking.dto.response;

import com.example.parking.entity.enums.Role;
import com.example.parking.entity.enums.UserCategory;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        Role role,
        UserCategory category,
        boolean enabled,
        int vehicleCount,
        LocalDateTime createdAt) {
}
