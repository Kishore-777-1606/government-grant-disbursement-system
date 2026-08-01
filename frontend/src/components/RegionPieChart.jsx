import React from "react";
import { dashboardData } from "../data/dashboardData";

import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const COLORS = ["#0d6efd", "#28a745", "#ffc107", "#dc3545"];

function RegionPieChart() {
  return (
    <div style={{ height: 300 }}>
      <ResponsiveContainer>
        <PieChart>
          <Pie
            data={dashboardData.regionDistribution}
            dataKey="value"
            nameKey="name"
            outerRadius={100}
            label
          >
            {dashboardData.regionDistribution.map((entry, index) => (
              <Cell
                key={index}
                fill={COLORS[index % COLORS.length]}
              />
            ))}
          </Pie>

          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}

export default RegionPieChart;