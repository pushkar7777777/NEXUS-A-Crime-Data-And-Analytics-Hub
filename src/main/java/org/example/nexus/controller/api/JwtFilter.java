package org.example.nexus.controller.api;

import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.nexus.util.JwtUtil;

import java.io.IOException;

/**
 * Filter to validate JWT tokens on API endpoints.
 * Exempts /api/v1/auth/login from authentication.
 */
@WebFilter("/api/v1/*")
public class JwtFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // Exempt login endpoint
        String path = req.getRequestURI();
        if (path.endsWith("/api/v1/auth/login")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Extract Authorization header
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Missing or invalid Authorization header\"}");
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        Claims claims = JwtUtil.validateToken(token);

        if (claims == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Invalid or expired token\"}");
            return;
        }

        // Set attributes on request for downstream handlers
        req.setAttribute("user_email", claims.getSubject());
        req.setAttribute("user_role", claims.get("role"));

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}

