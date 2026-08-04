import React from "react";

import MainLayout from "../../layouts/MainLayout";
import DashboardCards from "../../components/DashboardCards";

import FundBarChart from "../../components/FundBarChart";
import RegionBarChart from "../../components/RegionBarChart";
import CategoryPieChart from "../../components/CategoryPieChart";
import BudgetExhaustionChart from "../../components/BudgetExhaustionChart";
import ApprovalTurnaroundChart from "../../components/ApprovalTurnaroundChart";

import PendingMilestones from "../../components/PendingMilestones";
import RecentActivities from "../../components/RecentActivities";

function Analytics() {
  const cardStyle = {
    background: "#fff",
    padding: "20px",
    borderRadius: "12px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
    transition: "0.3s ease",
    cursor: "pointer",
  };

  return (
    <MainLayout>
      <div style={{ padding: "20px" }}>
        {/* Header */}
        <div
          style={{
            textAlign: "center",
            marginBottom: "30px",
          }}
        >
          <h1
            style={{
              color: "#1976d2",
              marginBottom: "10px",
            }}
          >
            Analytics Dashboard
          </h1>

          <p
            style={{
              color: "#666",
              fontSize: "16px",
            }}
          >
            Monitor government grant allocation, beneficiary progress,
            fund utilization and approval workflow.
          </p>
        </div>

        <DashboardCards />

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit,minmax(450px,1fr))",
            gap: "20px",
            marginTop: "30px",
          }}
        >
          <div
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-5px)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0px)";
            }}
          >
            <h2>Scheme-wise Fund Utilization</h2>
            <FundBarChart />
          </div>

          <div
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-5px)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0px)";
            }}
          >
            <h2>Region-wise Fund Utilization</h2>
            <RegionBarChart />
          </div>

          <div
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-5px)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0px)";
            }}
          >
            <h2>Category-wise Distribution</h2>
            <CategoryPieChart />
          </div>

          <div
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-5px)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0px)";
            }}
          >
            <h2>Budget Allocation vs Disbursement</h2>
            <BudgetExhaustionChart />
          </div>

          <div
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-5px)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0px)";
            }}
          >
            <h2>Approval Turnaround Time</h2>
            <ApprovalTurnaroundChart />
          </div>
        </div>

        <PendingMilestones />

        <RecentActivities />

        {/* Footer */}
        <div
          style={{
            textAlign: "center",
            marginTop: "40px",
            padding: "20px",
            color: "#777",
            borderTop: "1px solid #ddd",
          }}
        >
          © 2026 Government Grant Disbursement Tracking System
        </div>
      </div>
    </MainLayout>
  );
}

export default Analytics;