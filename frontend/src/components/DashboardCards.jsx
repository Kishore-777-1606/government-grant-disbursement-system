import React from "react";
import { summaryData } from "../data/dashboardData";

function DashboardCards() {
  const cardStyle = {
    background: "white",
    padding: "20px",
    borderRadius: "10px",
    width: "230px",
    textAlign: "center",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.2)",
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-around",
        flexWrap: "wrap",
        margin: "30px",
        gap: "20px",
      }}
    >
      <div style={cardStyle}>
        <h3>Total Beneficiaries</h3>
        <h2>{summaryData.totalBeneficiaries}</h2>
      </div>

      <div style={cardStyle}>
        <h3>Total Schemes</h3>
        <h2>{summaryData.totalSchemes}</h2>
      </div>

      <div style={cardStyle}>
        <h3>Funds Released</h3>
        <h2>{summaryData.fundsReleased}</h2>
      </div>

      <div style={cardStyle}>
        <h3>Pending Milestones</h3>
        <h2>{summaryData.pendingMilestones}</h2>
      </div>
    </div>
  );
}

export default DashboardCards;