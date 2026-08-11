import React, { useEffect, useState } from "react";

import {
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  Divider,
} from "@mui/material";


import {
  getRecentActivities,
} from "../api/analyticsApi";



function RecentActivities() {


const [activities,setActivities] = useState([]);




useEffect(()=>{


getRecentActivities()

.then((res)=>{


console.log(
"Activities:",
res.data
);


setActivities(
res.data || []
);


})


.catch((err)=>{


console.log(
"Activities Error:",
err
);


setActivities([]);


});


},[]);






return (


<Paper

sx={{

p:3,

borderRadius:5,

height:450,

overflow:"auto"

}}

>



{

activities.length === 0 ?


<Typography

sx={{

mt:10,

textAlign:"center",

color:"text.secondary"

}}

>

📝 No recent activities found

</Typography>



:


<List>


{

activities.map((item,index)=>(


<React.Fragment key={index}>


<ListItem

sx={{

borderRadius:3,

mb:1,

background:"#f8f9ff"

}}

>


<ListItemText


primary={

item.message ||

item.activity ||

item.description ||

"No activity"

}


secondary={

item.date ||

item.createdDate ||

""

}


/>


</ListItem>


<Divider/>


</React.Fragment>


))


}



</List>



}



</Paper>


);


}


export default RecentActivities;