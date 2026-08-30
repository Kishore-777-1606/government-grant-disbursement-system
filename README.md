# Government Grant/Subsidy Disbursement Tracking System

A full-stack platform that streamlines the complete lifecycle of a government subsidy/grant — from beneficiary registration through eligibility scoring, multi-level field and administrative verification, staged fund disbursement tied to compliance milestones, and fund-utilization analytics.

Built to replace manual, fragmented disbursement processes with a single system offering consistent verification governance, role-based access, and a complete audit trail.

---

## 🔗 Live Demo

- **Frontend:** https://government-grant-disbursement-syste.vercel.app
- **Backend API:** https://grant-disbursement-backend.onrender.com/api

> Note: the backend is hosted on Render's free tier, which spins down after periods of inactivity — the first request after a while may take 30–60 seconds to respond while it wakes up.

### Test Credentials

Log in with any of the following seeded accounts to evaluate each role:

| Role | Username | Password |
|---|---|---|
| Field Officer | `field1` | `Passw0rd@123` |
| District Officer | `district1` | `Passw0rd@123` |
| Finance Approver | `finance1` | `Passw0rd@123` |
| Admin | `admin` | `Passw0rd@123` |

---

## ✨ Features

- **Centralized beneficiary and scheme management** — registration, document upload, and scheme master data with eligibility criteria and grant amount slabs
- **Automated eligibility scoring** — configurable, criteria-based scoring that auto-routes applications for verification
- **Multi-level verification and approval** — Field Officer → District Officer → Finance Approver workflow with role-based routing
- **Milestone-based staged disbursement** — grant amounts released in installments tied to compliance milestone completion
- **JWT-based authentication and RBAC** — stateless auth with method-level `@PreAuthorize` security enforced across every backend endpoint
- **Audit logging** — every verification decision, approval, and disbursement action recorded with old/new values, actor, and timestamp
- **Analytics and reporting** — scheme-wise and region-wise fund utilization dashboards with Excel/PDF export

---

## 🛠️ Tech Stack

**Backend:** Java, Spring Boot, Spring Security, Spring Data JPA (Hibernate), MySQL, JWT
**Frontend:** React, Vite, Material UI (MUI), Axios, React Router
**Deployment:** Render (backend), Vercel (frontend), Railway (MySQL)

---

## 🚀 Running Locally

### Prerequisites
- Java 17+, Maven
- Node.js 18+
- MySQL 8.0+

### 1. Clone the repository
```bash
git clone https://github.com/Kishore-777-1606/government-grant-disbursement-system.git
cd government-grant-disbursement-system
```

### 2. Set up the database
```bash
mysql -u root -p < backend/database/schema.sql
mysql -u root -p < backend/database/sample_data.sql
```

### 3. Set required environment variables
The backend reads its JWT signing secret and database password from environment variables — there are no hardcoded fallback values, so these must be set or the app will fail to start.

```bash
# Generate a secret:
node -e "console.log(require('crypto').randomBytes(64).toString('hex'))"
```

**PowerShell:**
```powershell
$env:JWT_SECRET="<generated-secret>"
$env:DB_PASSWORD="<your-local-mysql-password>"
```

**macOS/Linux:**
```bash
export JWT_SECRET="<generated-secret>"
export DB_PASSWORD="<your-local-mysql-password>"
```

### 4. Run the backend
```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`.

### 5. Run the frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173`.

---

## 👥 Team & Roles (Milestone 4)

| Name | Area |
|---|---|
| Niharika | Core Security Architecture & RBAC — authentication, JWT, method-level security |
| Kishore | API Security & External Integrations — endpoint role checks, treasury/beneficiary integration |
| Ravi | Audit Logging Module |
| Sreelaxmi | Frontend Authentication & Access Control |
| Akshara | Deployment, Documentation & QA |

---

## 📄 License

This project is licensed under the [MIT License](./LICENSE).
