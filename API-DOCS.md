# NEXUS API Documentation

## Base URL
- Local: `http://localhost:8080/api/v1`
- Production: `https://<your-domain>/api/v1`

## Authentication

All API endpoints (except login) require a JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

### Login Endpoint

**POST /api/v1/auth/login**

Request body (form-encoded or JSON):
```json
{
  "email": "user@example.com",
  "password": "password123",
  "role": "CIVILIAN" // CIVILIAN, POLICE, or ADMIN
}
```

Response (201 Created on success):
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@example.com",
  "role": "CIVILIAN"
}
```

Response (401 Unauthorized on failure):
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

---

## Complaints Endpoints

### List Complaints

**GET /api/v1/complaints**

Query parameters:
- `regId` (optional): Filter by civilian registration ID

Headers:
```
Authorization: Bearer <token>
```

Response (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "complaintId": 1,
      "complaintType": "Burglary",
      "dateOfIncident": "2026-06-10",
      "locationOfIncident": "123 Main St",
      "latitude": 40.7128,
      "longitude": -74.0060,
      "description": "Someone broke into my house...",
      "currentStatus": "FILED",
      "filedBy": "John Doe",
      "dateFiled": "2026-06-12T10:30:00"
    }
  ]
}
```

### Create Complaint

**POST /api/v1/complaints**

Headers:
```
Authorization: Bearer <token>
Content-Type: application/json
```

Request body:
```json
{
  "regId": 5,
  "complaintType": "Theft",
  "locationOfIncident": "456 Oak Ave",
  "latitude": 40.7150,
  "longitude": -74.0088,
  "description": "My bike was stolen",
  "urgencyLevel": "HIGH"
}
```

Response (201 Created on success):
```json
{
  "success": true,
  "message": "Complaint created"
}
```

Response (500 Internal Server Error on failure):
```json
{
  "success": false,
  "message": "Failed to create complaint"
}
```

---

## Error Responses

### 400 Bad Request
Returned when required fields are missing or invalid.

### 401 Unauthorized
Returned when:
- Authorization header is missing or malformed
- Token is invalid or expired
- Credentials are incorrect (login endpoint)

### 500 Internal Server Error
Returned for unexpected server errors.

---

## Notes

- Token expiry: 24 hours
- All timestamps are in ISO 8601 format (UTC)
- JSON field names are camelCase
- Passwords are hashed using SHA-256 (consider upgrading to bcrypt in production)
- JWT secret is loaded from `JWT_SECRET` environment variable; defaults to a placeholder (change in production)

