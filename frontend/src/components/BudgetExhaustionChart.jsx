
import {

BarChart,

Bar,

XAxis,

YAxis,

Tooltip,

ResponsiveContainer

} from "recharts";



function BudgetExhaustionChart(){



const data=[


{

name:"Education Grant",

utilizationPercentage:40

},


{

name:"Farmer Support",

utilizationPercentage:100

}


];



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