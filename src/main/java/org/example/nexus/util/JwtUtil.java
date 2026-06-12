package org.example.nexus.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 * Utility for JWT token generation and validation.
 * Token validity: 24 hours.
 */
public class JwtUtil {

    private static final String SECRET_KEY = System.getenv().getOrDefault("JWT_SECRET", "nexus-secret-key-please-change-in-production-at-least-256-bits");
    private static final long VALIDITY_MS = 24 * 60 * 60 * 1000L; // 24 hours

    /**
     * Generate a JWT token with subject (email) and role claim.
     */
    public static String generateToken(String subject, String role) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + VALIDITY_MS);

        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    /**
     * Validate JWT token and extract claims.
     * @return Claims if valid, null if invalid/expired
     */
    public static Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            // token invalid or expired
            return null;
        }
    }

    /**
     * Extract role from token (convenience method).
     */
    public static String extractRole(String token) {
        Claims claims = validateToken(token);
        if (claims == null) return null;
        Object roleObj = claims.get("role");
        return roleObj == null ? null : roleObj.toString();
    }

    /**
     * Extract subject (email) from token.
     */
    public static String extractSubject(String token) {
        Claims claims = validateToken(token);
        return claims == null ? null : claims.getSubject();
    }

}

