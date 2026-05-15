package com.example.taskmanager.taskmanager_backend.security;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import java.security.Key;

import java.util.Date;

import java.util.HashMap;

import java.util.Map;

import java.util.function.Function;

@Component
public class JwtUtil {

    // ✅ SECRET KEY
    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkey";

    // ✅ KEY
    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes()
        );

    }

    // ✅ GENERATE TOKEN
    public String generateToken(
            UserDetails userDetails,
            String role
    ) {

        Map<String, Object> claims =
                new HashMap<>();

        // ✅ ADD ROLE
        claims.put("role", role);

        return Jwts.builder()

                .setClaims(claims)

                .setSubject(
                        userDetails.getUsername()
                )

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60
                        )
                )

                // ✅ FIXED METHOD
                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    // ✅ EXTRACT EMAIL
    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );

    }

    // ✅ EXTRACT EXPIRATION
    public Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );

    }

    // ✅ GENERIC CLAIM EXTRACTOR
    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        final Claims claims =
                extractAllClaims(token);

        return resolver.apply(claims);

    }

    // ✅ EXTRACT ALL CLAIMS
    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parserBuilder()

                .setSigningKey(
                        getSigningKey()
                )

                .build()

                .parseClaimsJws(token)

                .getBody();

    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(
            String token
    ) {

        try {

            extractAllClaims(token);

            return true;

        }

        catch (ExpiredJwtException e) {

            System.out.println("Token expired");

        }

        catch (UnsupportedJwtException e) {

            System.out.println("Unsupported token");

        }

        catch (MalformedJwtException e) {

            System.out.println("Malformed token");

        }

        catch (SignatureException e) {

            System.out.println("Invalid signature");

        }

        catch (IllegalArgumentException e) {

            System.out.println("Empty token");

        }

        return false;
    }

}