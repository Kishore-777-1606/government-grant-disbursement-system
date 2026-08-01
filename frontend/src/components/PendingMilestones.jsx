import React from "react";
import { pendingMilestones } from "../data/dashboardData";

function PendingMilestones() {
  return (
    <div
      style={{
        background: "white",
        margin: "20px",
        padding: "20px",
        borderRadius: "10px",
        boxShadow: "0 2px 5px rgba(0,0,0,0.2)",
      }}
    >
      <h2>📋 Pending Milestones</h2>

      <table
        style={{
          width: "100%",
          borderCollapse: "collapse",
        }}
      >
        <thead>
          <tr style={{ background: "#1976d2", color: "white" }}>
            <th style={{ padding: "10px" }}>Beneficiary</th>
            <th style={{ padding: "10px" }}>Scheme</th>
            <th style={{ padding: "10px" }}>Due Date</th>
            <th style={{ padding: "10px" }}>Status</th>
          </tr>
        </thead>

        <tbody>
          {pendingMilestones.map((item) => (
            <tr key={item.id}>
              <td style={{ padding: "10px", textAlign: "center" }}>
                {item.beneficiary}
              </td>

              <td style={{ padding: "10px", textAlign: "center" }}>
                {item.scheme}
              </td>

              <td style={{ padding: "10px", textAlign: "center" }}>
                {item.dueDate}
              </td>

              <td
                style={{
                  padding: "10px",
                  textAlign: "center",
                  color:
                    item.status === "Completed"
                      ? "green"
                      : "red",
                  fontWeight: "bold",
                }}
              >
                {item.status}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default PendingMilestones;