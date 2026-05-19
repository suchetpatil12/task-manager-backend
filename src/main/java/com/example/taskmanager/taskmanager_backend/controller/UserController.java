package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.dto.UserResponse;

import com.example.taskmanager.taskmanager_backend.entity.User;

import com.example.taskmanager.taskmanager_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/users")

public class UserController {

    @Autowired
    private UserService userService;

    // =========================================
    // GET ALL USERS
    // =========================================

    @GetMapping

    public List<UserResponse> getUsers() {

        return userService.getUsers();
    }

    // =========================================
    // GET DESIGNATIONS
    // =========================================

    @GetMapping("/designations")

    public List<String> getDesignations() {

        return userService.getDesignations();
    }

    // =========================================
    // USERS BY DESIGNATION
    // =========================================

    @GetMapping(
            "/by-designation/{designation}"
    )

    public List<User> getUsersByDesignation(

            @PathVariable String designation

    ) {

        return userService
                .getUsersByDesignation(
                        designation
                );
    }
}