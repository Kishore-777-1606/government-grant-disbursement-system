
import {

PieChart,

Pie,

Cell,

Tooltip,

Legend,

ResponsiveContainer

} from "recharts";



function RegionPieChart(){



const data=[


{
name:"State-1",
value:80000
},


{
name:"State-2",
value:50000
},


{
name:"State-3",
value:30000
}


];



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