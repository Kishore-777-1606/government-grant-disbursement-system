import React from "react";
import Navbar from "../components/Navbar";
import DashboardCards from "../components/DashboardCards";
import FundBarChart from "../components/FundBarChart";
import RegionPieChart from "../components/RegionPieChart";
import PendingMilestones from "../components/PendingMilestones";
import RecentActivities from "../components/RecentActivities";

function Dashboard() {
  return (
    <div
      style={{
        background: "#f4f6f9",
        minHeight: "100vh",
      }}
    >
      <Navbar />

      <h1 style={{ textAlign: "center", marginTop: "30px" }}>
        Analytics Dashboard
      </h1>

      <DashboardCards />

      {/* Charts Section */}
      <div
        style={{
          display: "flex",
          gap: "20px",
          margin: "30px",
          flexWrap: "wrap",
        }}
      >
        <div
          style={{
            flex: 1,
            minWidth: "450px",
            background: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0 2px 5px gray",
          }}
        >
          <h2>📊 Scheme-wise Fund Utilization</h2>
          <FundBarChart />
        </div>

        <div
          style={{
            flex: 1,
            minWidth: "450px",
            background: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0 2px 5px gray",
          }}
        >
          <h2>🥧 Region-wise Distribution</h2>
          <RegionPieChart />
        </div>
      </div>

      {/* Pending Milestones */}
      <PendingMilestones />

      {/* Recent Activities */}
      <RecentActivities />
    </div>
  );
}

export default Dashboard;