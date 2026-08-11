import {

PieChart,

Pie,

Cell,

Tooltip,

Legend,

ResponsiveContainer

} from "recharts";

import NoData from "./NoData";



// Consumes RegionUtilizationDTO[] from GET /api/analytics/region-utilization
// (region, totalAmount), passed down from Analytics.jsx as the `data` prop.
function RegionPieChart({ data: rawData }){


const data = (rawData || []).map((item) => ({
    name: item.region,
    value: item.totalAmount
}));



if(data.length === 0){

    return <NoData />;

}



const COLORS=[

"#1976d2",

"#2e7d32",

"#ed6c02"

];



return(


<ResponsiveContainer

width="100%"

height={300}

>


<PieChart>


<Pie

data={data}

dataKey="value"

nameKey="name"

cx="50%"

cy="50%"

outerRadius={100}

label

animationDuration={1000}

>


{

data.map((entry,index)=>(


<Cell

key={index}

fill={COLORS[index % COLORS.length]}

/>


))

}



</Pie>



<Tooltip/>


<Legend/>


</PieChart>



</ResponsiveContainer>


);


}



export default RegionPieChart;