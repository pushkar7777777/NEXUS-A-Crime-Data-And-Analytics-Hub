package org.example.nexus.controller.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.nexus.dao.ComplaintRegistrationDAO;
import org.example.nexus.model.Complaint;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/complaints")
public class ComplaintApiServlet extends HttpServlet {

    private final ComplaintRegistrationDAO dao = new ComplaintRegistrationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApiBase base = new ApiBase();

        String regIdParam = req.getParameter("regId");
        List<Complaint> list;

        if (regIdParam != null) {
            try {
                int regId = Integer.parseInt(regIdParam);
                list = dao.getByUserId(regId);
            } catch (NumberFormatException e) {
                base.writeJson(resp, base.jsonError("Invalid regId"));
                return;
            }
        } else {
            list = dao.getAllComplaints();
        }

        // Build JSON array
        StringBuilder sb = new StringBuilder();
        sb.append('{').append("\"success\":true,\"data\":");
        sb.append('[');

        for (int i = 0; i < list.size(); i++) {
            Complaint c = list.get(i);
            sb.append('{');
            sb.append('"').append("complaintId").append('"').append(':').append(c.getComplaintId()).append(',');
            appendJsonField(sb, "complaintType", c.getComplaintType());
            sb.append(',');
            appendJsonField(sb, "dateOfIncident", c.getDateOfIncident() == null ? null : c.getDateOfIncident().toString());
            sb.append(',');
            appendJsonField(sb, "locationOfIncident", c.getLocationOfIncident());
            sb.append(',');
            sb.append('"').append("latitude").append('"').append(':').append(c.getLatitude()).append(',');
            sb.append('"').append("longitude").append('"').append(':').append(c.getLongitude()).append(',');
            appendJsonField(sb, "description", c.getDescription());
            sb.append(',');
            appendJsonField(sb, "currentStatus", c.getCurrentStatus());
            sb.append(',');
            appendJsonField(sb, "filedBy", c.getFiledBy());
            sb.append(',');
            appendJsonField(sb, "dateFiled", c.getDateFiled() == null ? null : c.getDateFiled().toString());
            sb.append('}');
            if (i < list.size() - 1) sb.append(',');
        }

        sb.append(']').append('}');

        base.writeJson(resp, sb.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApiBase base = new ApiBase();

        // Accept either form-encoded or simple JSON bodies
        String contentType = req.getContentType();
        Complaint c = new Complaint();

        if (contentType != null && contentType.contains("application/json")) {
            StringBuilder jb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) jb.append(line);
            }
            String body = jb.toString();
            // very small utility parsing (assumes flat JSON and simple values)
            c.setRegId(parseInt(body, "regId", 0));
            c.setComplaintType(parseString(body, "complaintType"));
            c.setLocationOfIncident(parseString(body, "locationOfIncident"));
            c.setLatitude(parseDouble(body, "latitude", 0));
            c.setLongitude(parseDouble(body, "longitude", 0));
            c.setDescription(parseString(body, "description"));
            c.setUrgencyLevel(parseString(body, "urgencyLevel"));
        } else {
            // form parameters
            c.setRegId(parseIntFromString(req.getParameter("regId"), 0));
            c.setComplaintType(req.getParameter("complaintType"));
            c.setLocationOfIncident(req.getParameter("locationOfIncident"));
            c.setLatitude(parseDoubleFromString(req.getParameter("latitude"), 0));
            c.setLongitude(parseDoubleFromString(req.getParameter("longitude"), 0));
            c.setDescription(req.getParameter("description"));
            c.setUrgencyLevel(req.getParameter("urgencyLevel"));
        }

        boolean ok = dao.insert(c);
        if (ok) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            base.writeJson(resp, base.jsonOk("Complaint created"));
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            base.writeJson(resp, base.jsonError("Failed to create complaint"));
        }
    }

    // helpers
    private void appendJsonField(StringBuilder sb, String key, String value) {
        sb.append('"').append(key).append('"').append(':');
        if (value == null) sb.append("null");
        else sb.append('"').append(escape(value)).append('"');
    }

    private String escape(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private int parseInt(String body, String key, int fallback) {
        String s = parseString(body, key);
        if (s == null || s.isBlank()) return fallback;
        try { return Integer.parseInt(s); } catch (Exception e) { return fallback; }
    }

    private int parseIntFromString(String s, int fallback) {
        if (s == null || s.isBlank()) return fallback;
        try { return Integer.parseInt(s); } catch (Exception e) { return fallback; }
    }

    private double parseDouble(String body, String key, double fallback) {
        String s = parseString(body, key);
        if (s == null || s.isBlank()) return fallback;
        try { return Double.parseDouble(s); } catch (Exception e) { return fallback; }
    }

    private double parseDoubleFromString(String s, double fallback) {
        if (s == null || s.isBlank()) return fallback;
        try { return Double.parseDouble(s); } catch (Exception e) { return fallback; }
    }

    private String parseString(String body, String key) {
        if (body == null) return null;
        // crude: look for "key":"value" or "key":value
        String q = '"' + key + '"' + ':';
        int idx = body.indexOf(q);
        if (idx == -1) return null;
        int start = idx + q.length();
        // skip spaces
        while (start < body.length() && Character.isWhitespace(body.charAt(start))) start++;
        if (start >= body.length()) return null;
        if (body.charAt(start) == '"') {
            start++;
            int end = body.indexOf('"', start);
            if (end == -1) return body.substring(start);
            return body.substring(start, end);
        } else {
            // number or null
            int end = start;
            while (end < body.length() && ",}\n\r \t".indexOf(body.charAt(end))==-1) end++;
            return body.substring(start, end).replaceAll("[\"\\']", "");
        }
    }
}

