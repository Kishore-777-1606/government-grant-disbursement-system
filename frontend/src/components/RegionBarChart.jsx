import { useEffect, useState } from "react";

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

import { getRegionUtilization } from "../api/analyticsApi";


function RegionBarChart() {

  const [regionData, setRegionData] = useState([]);


  useEffect(() => {

    getRegionUtilization()
      .then((response) => {

        console.log("Region API:", response.data);

        setRegionData(response.data);

      })
      .catch((error) => {

        console.log("Region API Error:", error);

      });

  }, []);



  return (

    <ResponsiveContainer width="100%" height={350}>

      <BarChart data={regionData}>

        <CartesianGrid strokeDasharray="3 3" />

        <XAxis dataKey="region" />

        <YAxis />

        <Tooltip />

        <Legend />


        <Bar
          dataKey="totalAmount"
          fill="#009688"
          name="Total Utilization"
        />


      </BarChart>

    </ResponsiveContainer>

  );
}


export default RegionBarChart;