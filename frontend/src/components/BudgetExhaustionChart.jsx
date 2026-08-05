import React from "react";
import { budgetData } from "../data/dashboardData";

import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend,
} from "recharts";

function BudgetExhaustionChart() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <BarChart data={budgetData}>
        <CartesianGrid strokeDasharray="3 3" />

        <XAxis dataKey="scheme" />

        <YAxis />

        <Tooltip />

        <Legend />

        <Bar
          dataKey="allocated"
          fill="#1976d2"
          name="Allocated"
          radius={[5, 5, 0, 0]}
        />

        <Bar
          dataKey="disbursed"
          fill="#9c27b0"
          name="Disbursed"
          radius={[5, 5, 0, 0]}
        />
      </BarChart>
    </ResponsiveContainer>
  );
}

export default BudgetExhaustionChart;