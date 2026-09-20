package com.example.parking.mapper;

import com.example.parking.dto.request.CreateUserRequest;
import com.example.parking.dto.request.UpdateUserRequest;
import com.example.parking.dto.response.UserResponse;
import com.example.parking.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCategory(),
                user.isEnabled(),
                user.getVehicles() != null ? user.getVehicles().size() : 0,
                user.getCreatedAt());
    }

    /** Le mot de passe est encode par le service, jamais par le mapper. */
    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setCategory(request.getCategory());
        user.setEnabled(request.isEnabled());
        return user;
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setCategory(request.getCategory());
        user.setEnabled(request.isEnabled());
    }

    public UpdateUserRequest toUpdateRequest(User user) {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName(user.getFirstName());
        request.setLastName(user.getLastName());
        request.setEmail(user.getEmail());
        request.setPhone(user.getPhone());
        request.setRole(user.getRole());
        request.setCategory(user.getCategory());
        request.setEnabled(user.isEnabled());
        return request;
    }
}
