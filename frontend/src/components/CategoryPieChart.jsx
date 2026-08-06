


import {

PieChart,

Pie,

Cell,

Tooltip,

Legend,

ResponsiveContainer

} from "recharts";



function CategoryPieChart(){


const data=[


{
name:"Agriculture",
value:1
},


{
name:"Education",
value:1
}


];



const COLORS=[

"#1976d2",

"#2e7d32"

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

fill={COLORS[index]}

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



export default CategoryPieChart;