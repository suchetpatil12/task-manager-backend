package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.dto.AuthRequest;
import com.example.taskmanager.taskmanager_backend.dto.AuthResponse;
import com.example.taskmanager.taskmanager_backend.dto.RefreshTokenRequest;

import com.example.taskmanager.taskmanager_backend.entity.User;

import com.example.taskmanager.taskmanager_backend.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")

@CrossOrigin(origins = "http://localhost:4200")

public class AuthController {

    @Autowired
    private AuthService authService;

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(

            @RequestBody User user

    ) {

        return ResponseEntity.ok(

                authService.register(user)

        );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(

            @RequestBody AuthRequest request

    ) {

        return ResponseEntity.ok(

                authService.login(request)

        );
    }

    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(

            @RequestBody RefreshTokenRequest request

    ) {

        return ResponseEntity.ok(

                authService.refreshToken(request)

        );
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    @PostMapping("/logout")
    public ResponseEntity<?> logout(

            @RequestHeader("Authorization")
            String authHeader,

            @RequestBody Map<String, String> request

    ) {

        return ResponseEntity.ok(

                authService.logout(
                        authHeader,
                        request
                )

        );
    }
}