import React from "react";
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

const approvalData = [
  {
    scheme: "Education",
    days: 5,
  },
  {
    scheme: "Farmer Support",
    days: 3,
  },
  {
    scheme: "Women Welfare",
    days: 6,
  },
  {
    scheme: "Solar Pump",
    days: 4,
  },
];

function ApprovalTurnaroundChart() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <BarChart data={approvalData}>
        <CartesianGrid strokeDasharray="3 3" />

        <XAxis dataKey="scheme" />

        <YAxis
          label={{
            value: "Days",
            angle: -90,
            position: "insideLeft",
          }}
        />

        <Tooltip />

        <Legend />

        <Bar
          dataKey="days"
          fill="#4CAF50"
          radius={[5, 5, 0, 0]}
        />
      </BarChart>
    </ResponsiveContainer>
  );
}

export default ApprovalTurnaroundChart;