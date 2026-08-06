
import {

BarChart,

Bar,

XAxis,

YAxis,

Tooltip,

Legend,

ResponsiveContainer

} from "recharts";



function FundBarChart(){


const data=[

{
name:"Education",
allocatedAmount:50000,
releasedAmount:30000
},

{
name:"Farmer Support",
allocatedAmount:30000,
releasedAmount:15000
}

];



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