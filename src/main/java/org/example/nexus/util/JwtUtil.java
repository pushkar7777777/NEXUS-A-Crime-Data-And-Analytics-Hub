package org.example.nexus.util;

/**
 * Minimal JWT utility placeholder.
 *
 * NOTE: This is a placeholder. For production use replace with a proper JWT library
 * (jjwt or jose4j) and sign tokens using a secure secret loaded from environment.
 */
public class JwtUtil {

    // TODO: implement with secure JWT library
    public static String generateToken(String subject, String role) {
        // very small placeholder token: subject:role:timestamp (not secure)
        long ts = System.currentTimeMillis() / 1000L;
        return subject + ":" + role + ":" + ts;
    }

    public static boolean validateToken(String token) {
        // placeholder always true if token not null
        return token != null && token.contains(":");
    }

}

