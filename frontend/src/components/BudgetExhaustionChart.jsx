import {

BarChart,

Bar,

XAxis,

YAxis,

Tooltip,

ResponsiveContainer

} from "recharts";

import NoData from "./NoData";



// Consumes BudgetExhaustionDTO[] from GET /api/analytics/budget-exhaustion
// (schemeName, utilizationPercentage), passed down from Analytics.jsx as
// the `data` prop.
function BudgetExhaustionChart({ data: rawData }){



const data = (rawData || []).map((item) => ({
    name: item.schemeName,
    utilizationPercentage: item.utilizationPercentage
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


<YAxis domain={[0,100]}/>


<Tooltip/>


<Bar


dataKey="utilizationPercentage"

fill="#1976d2"

name="Utilization %"

radius={[8,8,0,0]}

animationDuration={1200}


/>


</BarChart>


</ResponsiveContainer>


);


}



export default BudgetExhaustionChart;