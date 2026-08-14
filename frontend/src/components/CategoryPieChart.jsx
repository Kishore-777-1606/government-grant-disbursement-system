import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import NoData from "./NoData";

// Consumes CategoryDistributionDTO[] from GET /api/analytics/category-distribution
// (category, count), passed down from Analytics.jsx as the `data` prop.
function CategoryPieChart({ data: rawData }) {
  const data = (rawData || []).map((item) => ({
    name: item.category,
    value: item.count,
  }));

  if (data.length === 0) {
    return <NoData />;
  }

  const COLORS = ["#1976d2", "#2e7d32", "#ed6c02", "#9c27b0", "#d32f2f"];

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

        <Tooltip />
        <Legend verticalAlign="bottom" height={36} />
      </PieChart>
    </ResponsiveContainer>
  );
}

export default CategoryPieChart;