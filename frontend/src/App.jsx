import { Routes, Route } from "react-router-dom";
import Login from "./pages/login/Login";
import Dashboard from "./pages/dashboard/Dashboard";
import Beneficiaries from "./pages/beneficiaries/beneficiaries";
import Schemes from "./pages/Schemes/Schemes";
import Applications from "./pages/Applications/Applications";
import Eligibility from "./pages/Eligibility/Eligibility";
import FinanceApproval from "./pages/FinanceApproval/FinanceApproval";
import Verification from "./pages/Verification/Verification";
import StatusTracking from "./pages/StatusTracking/StatusTracking";
import Analytics from "./pages/Analytics/Analytics";
import Disbursement from "./pages/Disbursement/Disbursement";

import ProtectedRoute from "./auth/ProtectedRoute";
import Unauthorized from "./pages/Unauthorized";
import AuditLog from "./pages/AuditLog/AuditLog";

function App() {
  return (
    <Routes>

      {/* ================= PUBLIC ROUTES ================= */}

      <Route path="/" element={<Login />} />

      <Route path="/login" element={<Login />} />

      <Route
        path="/unauthorized"
        element={<Unauthorized />}
      />


      {/* ================= COMMON AUTHENTICATED ROUTES ================= */}

      <Route
        element={
          <ProtectedRoute
            allowedRoles={[
              "FIELD_OFFICER",
              "DISTRICT_OFFICER",
              "FINANCE_APPROVER",
              "ADMIN",
            ]}
          />
        }
      >
        <Route
          path="/dashboard"
          element={<Dashboard />}
        />

        <Route
          path="/beneficiaries"
          element={<Beneficiaries />}
        />

        <Route
          path="/applications"
          element={<Applications />}
        />

        <Route
          path="/eligibility"
          element={<Eligibility />}
        />

        <Route
          path="/status"
          element={<StatusTracking />}
        />
      </Route>


      {/* ================= SCHEMES ================= */}

      <Route
        element={
          <ProtectedRoute
            allowedRoles={[
              "DISTRICT_OFFICER",
              "FINANCE_APPROVER",
              "ADMIN",
            ]}
          />
        }
      >
        <Route
          path="/schemes"
          element={<Schemes />}
        />
      </Route>


      {/* ================= VERIFICATION ================= */}

      <Route
        element={
          <ProtectedRoute
            allowedRoles={[
              "FIELD_OFFICER",
              "DISTRICT_OFFICER",
              "ADMIN",
            ]}
          />
        }
      >
        <Route
          path="/verification"
          element={<Verification />}
        />
      </Route>


      {/* ================= FINANCE / DISBURSEMENT / ANALYTICS ================= */}

      <Route
        element={
          <ProtectedRoute
            allowedRoles={[
              "FINANCE_APPROVER",
              "ADMIN",
            ]}
          />
        }
      >
        <Route
          path="/finance"
          element={<FinanceApproval />}
        />

        <Route
          path="/disbursement"
          element={<Disbursement />}
        />

        <Route
          path="/analytics"
          element={<Analytics />}
        />
      </Route>


      {/* ================= AUDIT LOG ================= */}

      <Route
        element={
          <ProtectedRoute
            allowedRoles={[
              "DISTRICT_OFFICER",
              "ADMIN",
            ]}
          />
        }
      >
        <Route
          path="/audit-log"
          element={<AuditLog />}
        />
      </Route>

    </Routes>
  );
}

export default App;