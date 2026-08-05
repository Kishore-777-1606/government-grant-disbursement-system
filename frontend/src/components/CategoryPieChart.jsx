import React from "react";
import { categoryData } from "../data/dashboardData";

import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const COLORS = ["#1976d2", "#4caf50", "#ff9800", "#e91e63"];

function CategoryPieChart() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <PieChart>
        <Pie
          data={categoryData}
          dataKey="beneficiaries"
          nameKey="category"
          cx="50%"
          cy="50%"
          outerRadius={120}
          label
        >
          {categoryData.map((entry, index) => (
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
  );
}

export default CategoryPieChart;