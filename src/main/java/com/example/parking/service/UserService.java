package com.example.parking.service;

import com.example.parking.dto.request.CreateUserRequest;
import com.example.parking.dto.request.UpdateUserRequest;
import com.example.parking.dto.response.UserResponse;
import com.example.parking.entity.User;
import com.example.parking.entity.enums.Role;
import com.example.parking.entity.enums.UserCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<UserResponse> search(String term, Role role, UserCategory category, Boolean enabled, Pageable pageable);

    List<UserResponse> findAll();

    UserResponse getById(Long id);

    User getEntity(Long id);

    User getByUsername(String username);

    UserResponse create(CreateUserRequest request);

    UserResponse update(Long id, UpdateUserRequest request);

    void delete(Long id);
}
