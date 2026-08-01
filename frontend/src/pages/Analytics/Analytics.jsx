import React from "react";

import Navbar from "../../components/Navbar";
import DashboardCards from "../../components/DashboardCards";

import FundBarChart from "../../components/FundBarChart";
import RegionBarChart from "../../components/RegionBarChart";
import CategoryPieChart from "../../components/CategoryPieChart";
import BudgetExhaustionChart from "../../components/BudgetExhaustionChart";

import PendingMilestones from "../../components/PendingMilestones";
import RecentActivities from "../../components/RecentActivities";

function Analytics() {
  return (
    <div
      style={{
        background: "#f4f6f9",
        minHeight: "100vh",
      }}
    >
      <Navbar />

      <h1
        style={{
          textAlign: "center",
          margin: "25px",
        }}
      >
        Analytics Dashboard
      </h1>

      <DashboardCards />

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr",
          gap: "20px",
          padding: "20px",
        }}
      >
        <div
          style={{
            background: "white",
            padding: "20px",
            borderRadius: "10px",
          }}
        >
          <h2>Scheme-wise Fund Utilization</h2>
          <FundBarChart />
        </div>

        <div
          style={{
            background: "white",
            padding: "20px",
            borderRadius: "10px",
          }}
        >
          <h2>Region-wise Fund Utilization</h2>
          <RegionBarChart />
        </div>

        <div
          style={{
            background: "white",
            padding: "20px",
            borderRadius: "10px",
          }}
        >
          <h2>Category-wise Distribution</h2>
          <CategoryPieChart />
        </div>

        <div
          style={{
            background: "white",
            padding: "20px",
            borderRadius: "10px",
          }}
        >
          <h2>Budget Exhaustion</h2>
          <BudgetExhaustionChart />
        </div>
      </div>

      <PendingMilestones />

      <RecentActivities />
    </div>
  );
}

export default Analytics;