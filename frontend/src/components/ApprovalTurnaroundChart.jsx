import { useEffect, useState } from "react";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";


import {
  Paper,
  Typography
} from "@mui/material";


import {
  getApprovalTurnaround
} from "../api/analyticsApi";



function ApprovalTurnaroundChart() {


const [data,setData] = useState([]);



useEffect(()=>{


getApprovalTurnaround()

.then((res)=>{


console.log(
"Approval Turnaround Data:",
res.data
);


setData(
res.data || []
);


})

.catch((err)=>{


console.log(
"Approval API Error:",
err
);


setData([]);


});


},[]);






return (


<Paper

sx={{

p:3,

borderRadius:5,

height:450,

display:"flex",

flexDirection:"column"

}}

>





{

data.length === 0 ?


<Typography

sx={{

mt:15,

textAlign:"center"

}}

>

No approval data available

</Typography>



:


<ResponsiveContainer

width="100%"

height={350}

>


<BarChart

data={data}

margin={{

top:20,

right:20,

left:10,

bottom:20

}}

>


<XAxis

dataKey="stage"

/>



<YAxis

label={{

value:"Days",

angle:-90,

position:"insideLeft"

}}

/>



<Tooltip />




<Bar

dataKey="days"

fill="#1976d2"

radius={[8,8,0,0]}

/>



</BarChart>


</ResponsiveContainer>



}


</Paper>


);


}


export default ApprovalTurnaroundChart;