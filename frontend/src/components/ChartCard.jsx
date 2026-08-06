
import {
  Paper,
  Typography,
} from "@mui/material";


function ChartCard({title,children}){


return(

<Paper

elevation={4}

sx={{

p:2,

borderRadius:4,

minHeight:380,

transition:"0.3s",

"&:hover":{

transform:"translateY(-5px)",

boxShadow:10

}

}}

>


<Typography

variant="h6"

fontWeight="bold"

mb={2}

>

{title}

</Typography>


{children}


</Paper>

);


}


export default ChartCard;