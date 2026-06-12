package org.example.nexus.controller.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.nexus.dao.AdminRegistrationDAO;
import org.example.nexus.dao.CivilianRegistrationDAO;
import org.example.nexus.dao.PoliceRegistrationDAO;
import org.example.nexus.util.JwtUtil;
import org.example.nexus.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/v1/auth/login")
public class AuthApiServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApiBase base = new ApiBase();

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        if (email == null || password == null || role == null || email.isBlank() || password.isBlank() || role.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            base.writeJson(resp, base.jsonError("Missing email, password, or role"));
            return;
        }

        String token = null;
        String userEmail = null;

        try {
            switch (role.toUpperCase()) {
                case "CIVILIAN": {
                    CivilianRegistrationDAO cdao = new CivilianRegistrationDAO();
                    String hashed = PasswordUtil.hashPassword(password);
                    Object civilian = cdao.checkLogin(email, hashed);
                    if (civilian != null) {
                        token = JwtUtil.generateToken(email, "CIVILIAN");
                        userEmail = email;
                    }
                    break;
                }
                case "POLICE": {
                    PoliceRegistrationDAO pdao = new PoliceRegistrationDAO();
                    Object police = pdao.checkLogin(email, password);
                    if (police != null) {
                        token = JwtUtil.generateToken(email, "POLICE");
                        userEmail = email;
                    }
                    break;
                }
                case "ADMIN": {
                    AdminRegistrationDAO adao = new AdminRegistrationDAO();
                    String hashed = PasswordUtil.hashPassword(password);
                    Object admin = adao.checkLogin(email, hashed);
                    if (admin != null) {
                        token = JwtUtil.generateToken(email, "ADMIN");
                        userEmail = email;
                    }
                    break;
                }
                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    base.writeJson(resp, base.jsonError("Invalid role"));
                    return;
            }

            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                base.writeJson(resp, base.jsonError("Invalid credentials"));
                return;
            }

            // success
            resp.setStatus(HttpServletResponse.SC_OK);
            String json = String.format("{\"success\":true,\"message\":\"Login successful\",\"token\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}", token, userEmail, role);
            base.writeJson(resp, json);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            base.writeJson(resp, base.jsonError("Internal error: " + e.getMessage()));
        }
    }
}

