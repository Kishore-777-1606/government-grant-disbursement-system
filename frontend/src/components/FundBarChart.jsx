import React from "react";
import { schemeData } from "../data/dashboardData";

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

function FundBarChart() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <BarChart data={schemeData}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="scheme" />
        <YAxis />
        <Tooltip />
        <Legend />

        <Bar
          dataKey="allocated"
          fill="#1976d2"
          name="Allocated Funds"
        />

        <Bar
          dataKey="disbursed"
          fill="#4caf50"
          name="Disbursed Funds"
        />
      </BarChart>
    </ResponsiveContainer>
  );
}

export default FundBarChart;