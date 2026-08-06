
import {
Card,
CardContent,
Typography,
Box
} from "@mui/material";


function AnimatedDashboardCard({
title,
value,
icon,
gradient
}){


return(

<Card

sx={{
height:160,
borderRadius:4,
background:gradient,
color:"white",
transition:"0.3s",

"&:hover":{
transform:"translateY(-8px)",
boxShadow:8
}

}}

>


<CardContent>


<Box

sx={{
display:"flex",
justifyContent:"space-between",
alignItems:"center"
}}

>


<Box>

<Typography
variant="subtitle1"
>
{title}
</Typography>


<Typography

variant="h3"

fontWeight="bold"

>

{value}

</Typography>


</Box>


<Box
fontSize={45}
>

{icon}

</Box>


</Box>


</CardContent>


</Card>


)


}


export default AnimatedDashboardCard;