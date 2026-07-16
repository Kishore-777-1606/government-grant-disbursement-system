# REST API List

## Base URL

```text
http://localhost:8080/api/v1
```

---

# Authentication

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /auth/login | User Login |

---

c

---

# Application Module

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /applications | Submit Application |
| GET | /applications | Get All Applications |
| GET | /applications/{id} | Get Application |
| PUT | /applications/{id} | Update Application |
| DELETE | /applications/{id} | Withdraw/Delete Application |

---

# Field Officer Module

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | /field-officer/applications | View Assigned Applications |
| POST | /field-officer/verify/{applicationId} | Submit Verification |

---

# District Officer Module

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | /district-officer/applications | View Verified Applications |
| POST | /district-officer/review/{applicationId} | Approve / Reject Application |

---

# Finance Module

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | /finance/applications | View Approved Applications |
| POST | /finance/approve/{applicationId} | Final Approval |

---

# Disbursement Module

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /disbursement/schedule | Schedule Fund Release |
| POST | /disbursement/release | Release Funds |
| GET | /disbursement/history | View Disbursement History |

---

# Reports Module

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | /reports/beneficiaries | Beneficiary Report |
| GET | /reports/schemes | Scheme Report |
| GET | /reports/districts | District-wise Report |
| GET | /reports/funds | Fund Utilization Report |

---

# Audit Log Module

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | /audit/logs | View Audit Logs |
| GET | /audit/logs?userId={id} | Filter Audit Logs |

---

## Total APIs

31 REST APIs