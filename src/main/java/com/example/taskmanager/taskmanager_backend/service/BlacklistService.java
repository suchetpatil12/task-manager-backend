package com.example.taskmanager.taskmanager_backend.service;

import com.example.taskmanager.taskmanager_backend.entity.BlacklistedToken;
import com.example.taskmanager.taskmanager_backend.repository.BlacklistedTokenRepository;
import com.example.taskmanager.taskmanager_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlacklistService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BlacklistedTokenRepository repo;

    public void blacklistToken(String token) {

        BlacklistedToken bt = new BlacklistedToken();

        bt.setToken(token);

        // ✅ extract expiry from JWT
        bt.setExpiryDate(
                jwtUtil.extractExpiration(token)
                        .toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        repo.save(bt);
    }

    public boolean isBlacklisted(String token) {
        return repo.existsByToken(token);
    }

    // 🔥 CLEANUP JOB (VERY IMPORTANT)
    @Scheduled(fixedRate = 3600000) // every 1 hour
    public void cleanExpiredTokens() {
        repo.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
