import {

BarChart,

Bar,

XAxis,

YAxis,

Tooltip,

Legend,

ResponsiveContainer

} from "recharts";

import NoData from "./NoData";



// Consumes FundUtilizationDTO[] from GET /api/analytics/fund-utilization
// (schemeName, totalAmount, releasedAmount, remainingAmount), passed down
// from Analytics.jsx as the `data` prop — mapped here to the field names
// the chart itself renders (name, allocatedAmount, releasedAmount).
function FundBarChart({ data: rawData }){


const data = (rawData || []).map((item) => ({
    name: item.schemeName,
    allocatedAmount: item.totalAmount,
    releasedAmount: item.releasedAmount
}));



if(data.length === 0){

    return <NoData />;

}



return(


<ResponsiveContainer

width="100%"

height={300}

>


<BarChart data={data}>


<XAxis dataKey="name"/>


<YAxis/>


<Tooltip/>


<Legend/>




<Bar

dataKey="allocatedAmount"

fill="#1976d2"

name="Allocated Amount"

radius={[8,8,0,0]}

animationDuration={1200}

/>



<Bar

dataKey="releasedAmount"

fill="#2e7d32"

name="Released Amount"

radius={[8,8,0,0]}

animationDuration={1200}

/>



</BarChart>



</ResponsiveContainer>


);


}


export default FundBarChart;