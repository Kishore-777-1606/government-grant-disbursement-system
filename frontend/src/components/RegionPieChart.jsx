import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import NoData from "./NoData";

// Consumes RegionUtilizationDTO[] from GET /api/analytics/region-utilization
// (region, totalAmount), passed down from Analytics.jsx as the `data` prop.
function RegionPieChart({ data: rawData }) {
  const data = (rawData || []).map((item) => ({
    name: item.region,
    value: item.totalAmount,
  }));

  if (data.length === 0) {
    return <NoData />;
  }

  const COLORS = ["#1976d2", "#2e7d32", "#ed6c02", "#9c27b0", "#0288d1"];

  // Percentage label instead of the raw amount — a raw number label on a
  // single-slice (100%) pie renders far outside the arc and overflows the card.
  const renderLabel = ({ percent }) => `${(percent * 100).toFixed(0)}%`;

  return (
    <ResponsiveContainer width="100%" height={320}>
      <PieChart margin={{ top: 10, right: 10, bottom: 10, left: 10 }}>
        <Pie
          data={data}
          dataKey="value"
          nameKey="name"
          cx="50%"
          cy="45%"
          outerRadius={85}
          label={renderLabel}
          labelLine={false}
          animationDuration={1000}
        >
          {data.map((entry, index) => (
            <Cell key={index} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>

        <Tooltip formatter={(value) => value.toLocaleString()} />
        <Legend verticalAlign="bottom" height={36} />
      </PieChart>
    </ResponsiveContainer>
  );
}

export default RegionPieChart;