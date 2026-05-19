package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.dto.UserResponse;

import com.example.taskmanager.taskmanager_backend.entity.User;

import com.example.taskmanager.taskmanager_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // =========================================
    // GET ALL USERS
    // =========================================

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {

        return userRepository.findAll()

                .stream()

                .map(user ->

                        UserResponse.builder()

                                .id(user.getId())

                                .name(user.getName())

                                .email(user.getEmail())

                                .designation(
                                        user.getDesignation()
                                )

                                .build()

                )

                .toList();
    }

    // =========================================
    // GET DESIGNATIONS
    // =========================================

    @Transactional(readOnly = true)
    public List<String> getDesignations() {

        return userRepository
                .findDistinctDesignations();
    }

    // =========================================
    // USERS BY DESIGNATION
    // =========================================

    @Transactional(readOnly = true)
    public List<User> getUsersByDesignation(
            String designation
    ) {

        return userRepository
                .findByDesignation(designation);
    }
}