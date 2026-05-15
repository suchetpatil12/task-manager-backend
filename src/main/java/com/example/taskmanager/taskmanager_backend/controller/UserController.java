package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.dto.UserResponse;
import com.example.taskmanager.taskmanager_backend.entity.Designation;
import com.example.taskmanager.taskmanager_backend.entity.User;
import com.example.taskmanager.taskmanager_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<UserResponse> getUsers() {

        return userRepository.findAll()

                .stream()

                .map(user -> UserResponse.builder()

                        .id(user.getId())

                        .name(user.getName())

                        .email(user.getEmail())

                        .designation(user.getDesignation())

                        .build()

                )

                .toList();
    }

    @GetMapping("/designations")
    public List<String> getDesignations() {

        return Arrays.stream(Designation.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/by-designation/{designation}")

    public List<User> getUsersByDesignation(
            @PathVariable Designation designation
    ) {

        return userRepository.findByDesignation(designation);

    }

}