import React from "react";
import { recentActivities } from "../data/dashboardData";

function RecentActivities() {
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
      <h2>📝 Recent Activities</h2>

      <ul style={{ lineHeight: "2" }}>
        {recentActivities.map((activity) => (
          <li key={activity.id}>
            {activity.activity}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default RecentActivities;