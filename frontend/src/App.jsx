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

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/beneficiaries" element={<Beneficiaries />} />
      <Route path="/schemes" element={<Schemes />} />
      <Route path="/applications" element={<Applications />} />
      <Route path="/eligibility" element={<Eligibility />} />
      <Route path="/verification" element={<Verification />} />
      <Route path="/finance" element={<FinanceApproval />} />
      <Route path="/status" element={<StatusTracking />} />
      <Route path="/analytics" element={<Analytics />} />
    </Routes>
  );
}

export default App;