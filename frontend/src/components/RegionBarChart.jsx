import React from "react";
import { regionData } from "../data/dashboardData";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
  Legend,
} from "recharts";

function RegionBarChart() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <BarChart data={regionData}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="region" />
        <YAxis />
        <Tooltip />
        <Legend />

        <Bar
          dataKey="allocated"
          fill="#ff9800"
          name="Allocated Funds"
        />

        <Bar
          dataKey="disbursed"
          fill="#009688"
          name="Disbursed Funds"
        />
      </BarChart>
    </ResponsiveContainer>
  );
}

export default RegionBarChart;