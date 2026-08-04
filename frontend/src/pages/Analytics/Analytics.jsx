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
  return (
    <MainLayout>
      <h1
        style={{
          textAlign: "center",
          margin: "25px",
          color: "#1976d2",
        }}
      >
        Analytics Dashboard
      </h1>

      <DashboardCards />

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(450px, 1fr))",
          gap: "20px",
          padding: "20px",
        }}
      >
        {/* Scheme-wise Fund Utilization */}
        <div
          style={{
            background: "#fff",
            padding: "20px",
            borderRadius: "12px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
          }}
        >
          <h2>Scheme-wise Fund Utilization</h2>
          <FundBarChart />
        </div>

        {/* Region-wise Fund Utilization */}
        <div
          style={{
            background: "#fff",
            padding: "20px",
            borderRadius: "12px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
          }}
        >
          <h2>Region-wise Fund Utilization</h2>
          <RegionBarChart />
        </div>

        {/* Category-wise Distribution */}
        <div
          style={{
            background: "#fff",
            padding: "20px",
            borderRadius: "12px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
          }}
        >
          <h2>Category-wise Distribution</h2>
          <CategoryPieChart />
        </div>

        {/* Budget Exhaustion */}
        <div
          style={{
            background: "#fff",
            padding: "20px",
            borderRadius: "12px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
          }}
        >
          <h2>Budget Exhaustion</h2>
          <BudgetExhaustionChart />
        </div>

        {/* Approval Turnaround Time */}
        <div
          style={{
            background: "#fff",
            padding: "20px",
            borderRadius: "12px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
          }}
        >
          <h2>Approval Turnaround Time</h2>
          <ApprovalTurnaroundChart />
        </div>
      </div>

      <PendingMilestones />

      <RecentActivities />
    </MainLayout>
  );
}

export default Analytics;