package org.example.nexus.controller.api;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiBase {

    protected void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    protected String jsonError(String message) {
        return String.format("{\"success\":false,\"message\":\"%s\"}", escape(message));
    }

    protected String jsonOk(String message) {
        return String.format("{\"success\":true,\"message\":\"%s\"}", escape(message));
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}

