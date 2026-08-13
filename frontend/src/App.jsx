import { Routes, Route } from "react-router-dom";
import FinanceApproval from "./pages/FinanceApproval/FinanceApproval";
import Login from "./pages/login/Login";
import Dashboard from "./pages/dashboard/Dashboard";
import Beneficiaries from "./pages/beneficiaries/beneficiaries";
import Schemes from "./pages/Schemes/Schemes";
import Applications from "./pages/Applications/Applications";
import Eligibility from "./pages/Eligibility/Eligibility";
import Verification from "./pages/Verification/Verification";
import StatusTracking from "./pages/StatusTracking/StatusTracking";
import Analytics from "./pages/Analytics/Analytics";
import Disbursement from "./pages/Disbursement/Disbursement";
import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/beneficiaries"
        element={
          <ProtectedRoute>
            <Beneficiaries />
          </ProtectedRoute>
        }
      />
      <Route
        path="/schemes"
        element={
          <ProtectedRoute>
            <Schemes />
          </ProtectedRoute>
        }
      />
      <Route
        path="/applications"
        element={
          <ProtectedRoute>
            <Applications />
          </ProtectedRoute>
        }
      />
      <Route
        path="/eligibility"
        element={
          <ProtectedRoute>
            <Eligibility />
          </ProtectedRoute>
        }
      />
      <Route
        path="/verification"
        element={
          <ProtectedRoute>
            <Verification />
          </ProtectedRoute>
        }
      />
      <Route
        path="/finance"
        element={
          <ProtectedRoute>
            <FinanceApproval />
          </ProtectedRoute>
        }
      />
      <Route
        path="/disbursement"
        element={
          <ProtectedRoute>
            <Disbursement />
          </ProtectedRoute>
        }
      />
      <Route
        path="/status"
        element={
          <ProtectedRoute>
            <StatusTracking />
          </ProtectedRoute>
        }
      />
      <Route
        path="/analytics"
        element={
          <ProtectedRoute>
            <Analytics />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;