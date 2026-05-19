package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.dto.AuthRequest;
import com.example.taskmanager.taskmanager_backend.dto.AuthResponse;
import com.example.taskmanager.taskmanager_backend.dto.RefreshTokenRequest;

import com.example.taskmanager.taskmanager_backend.entity.RefreshToken;
import com.example.taskmanager.taskmanager_backend.entity.Role;
import com.example.taskmanager.taskmanager_backend.entity.User;

import com.example.taskmanager.taskmanager_backend.repository.UserRepository;

import com.example.taskmanager.taskmanager_backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

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
    // REGISTER
    // =========================================================

    public Map<String, Object> register(
            User user
    ) {

        // EMAIL EXISTS

        if (

                userRepository
                        .findByEmail(user.getEmail())
                        .isPresent()

        ) {

            throw new RuntimeException(
                    "Email already exists"
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

        // SAVE

        userRepository.save(user);

        return Map.of(

                "success", true,

                "message",
                "User registered successfully"

        );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    public AuthResponse login(
            AuthRequest request
    ) {

        User user =

                userRepository
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

            throw new RuntimeException(
                    "Invalid credentials"
            );
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

        return new AuthResponse(

                accessToken,

                refreshToken

        );
    }

    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    public AuthResponse refreshToken(
            RefreshTokenRequest request
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

        return new AuthResponse(

                newAccessToken,

                requestToken

        );
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    public String logout(

            String authHeader,

            Map<String, String> request

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

        return "Logged out successfully";
    }
}