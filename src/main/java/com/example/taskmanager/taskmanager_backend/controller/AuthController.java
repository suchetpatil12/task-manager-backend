package com.example.taskmanager.taskmanager_backend.controller;

import com.example.taskmanager.taskmanager_backend.dto.AuthRequest;
import com.example.taskmanager.taskmanager_backend.dto.AuthResponse;
import com.example.taskmanager.taskmanager_backend.dto.RefreshTokenRequest;

import com.example.taskmanager.taskmanager_backend.entity.RefreshToken;
import com.example.taskmanager.taskmanager_backend.entity.Role;
import com.example.taskmanager.taskmanager_backend.entity.User;

import com.example.taskmanager.taskmanager_backend.repository.UserRepository;

import com.example.taskmanager.taskmanager_backend.security.JwtUtil;

import com.example.taskmanager.taskmanager_backend.service.BlacklistService;
import com.example.taskmanager.taskmanager_backend.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")

@CrossOrigin(origins = "http://localhost:4200")

public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private BlacklistService blacklistService;

    // =========================================================
    // REGISTER / SIGNUP
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user
    ) {

        // CHECK EMAIL EXISTS

        if (
                userRepository
                        .findByEmail(user.getEmail())
                        .isPresent()
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Email already exists"
                            )
                    );
        }

        // ENCODE PASSWORD

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        // DEFAULT ROLE

        if (user.getRole() == null) {

            user.setRole(Role.MEMBER);

        }

        // SAVE USER

        userRepository.save(user);

        return ResponseEntity

                .ok()

                .header(
                        "Content-Type",
                        "application/json"
                )

                .body(

                        Map.of(
                                "success", true,
                                "message", "User registered successfully"
                        )

                );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequest request
    ) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        // INVALID CREDENTIALS

        if (
                user == null
                        ||
                        !passwordEncoder.matches(
                                request.getPassword(),
                                user.getPassword()
                        )
        ) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid credentials");
        }

        // USER DETAILS

        UserDetails userDetails =

                org.springframework.security.core.userdetails.User

                        .withUsername(user.getEmail())

                        .password(user.getPassword())

                        .roles(user.getRole().name())

                        .build();

        // ACCESS TOKEN

        String accessToken =
                jwtUtil.generateToken(
                        userDetails,
                        user.getRole().name()
                );

        // REFRESH TOKEN

        String refreshToken =
                refreshTokenService
                        .createRefreshToken(user)
                        .getToken();

        // RESPONSE

        return ResponseEntity.ok(

                new AuthResponse(
                        accessToken,
                        refreshToken
                )

        );
    }

    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        String requestToken =
                request.getRefreshToken();

        RefreshToken refreshToken =

                refreshTokenService

                        .findByToken(requestToken)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid refresh token"
                                )
                        );

        refreshTokenService
                .verifyExpiration(refreshToken);

        // USER DETAILS

        UserDetails userDetails =

                org.springframework.security.core.userdetails.User

                        .withUsername(
                                refreshToken
                                        .getUser()
                                        .getEmail()
                        )

                        .password(
                                refreshToken
                                        .getUser()
                                        .getPassword()
                        )

                        .roles(
                                refreshToken
                                        .getUser()
                                        .getRole()
                                        .name()
                        )

                        .build();

        // NEW ACCESS TOKEN

        String newAccessToken =

                jwtUtil.generateToken(

                        userDetails,

                        refreshToken
                                .getUser()
                                .getRole()
                                .name()

                );

        return ResponseEntity.ok(

                new AuthResponse(
                        newAccessToken,
                        requestToken
                )

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

        String accessToken =
                authHeader.substring(7);

        String refreshToken =
                request.get("refreshToken");

        // BLACKLIST ACCESS TOKEN

        blacklistService
                .blacklistToken(accessToken);

        // DELETE REFRESH TOKEN

        refreshTokenService
                .deleteByToken(refreshToken);

        return ResponseEntity.ok(
                "Logged out successfully"
        );
    }
}