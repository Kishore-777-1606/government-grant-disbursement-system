# API Design

## Project

Government Subsidy & Grant Disbursement Tracking System

---

## API Standards

### Architecture

REST API

### Base URL

```text
http://localhost:8080/api/v1
```

### Authentication

JWT Bearer Token

### Data Format

JSON

### HTTP Methods

- GET
- POST
- PUT
- DELETE

---

## Standard Request Header

```http
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
```

---

## Standard Response Format

```json
{
  "success": true,
  "message": "Operation Successful",
  "data": {}
}
```

---

## Standard HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | OK |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## Validation Rules

- Aadhaar Number must contain 12 digits.
- Mobile Number must contain 10 digits.
- Annual Income must be greater than zero.
- Scheme Name cannot be empty.
- Beneficiary must exist before submitting an application.
- Duplicate applications for the same scheme are not allowed.
- Only authorized roles can access protected APIs.

---

## Role Based Access Control (RBAC)

### Admin

- Full system access

### Beneficiary

- Login
- Register
- Submit Application
- View Own Application

### Field Officer

- View Assigned Applications
- Verify Applications

### District Officer

- Review Applications
- Approve / Reject Applications

### Finance Officer

- Final Approval
- Schedule Fund Release
- Release Funds

### Auditor

- View Reports
- View Audit Logs