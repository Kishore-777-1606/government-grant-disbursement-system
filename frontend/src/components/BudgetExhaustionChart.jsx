import React from "react";
import { budgetData } from "../data/dashboardData";

import {
  BarChart,
  Bar,
  XAxis,
 YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";

function BudgetExhaustionChart() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <BarChart data={budgetData}>
        <CartesianGrid strokeDasharray="3 3" />

        <XAxis dataKey="name" />

        <YAxis />

        <Tooltip />

        <Bar
          dataKey="amount"
          fill="#9c27b0"
        />
      </BarChart>
    </ResponsiveContainer>
  );
}

export default BudgetExhaustionChart;