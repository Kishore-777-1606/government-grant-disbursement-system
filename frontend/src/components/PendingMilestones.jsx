import { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  Grid,
  Typography
} from "@mui/material";

import { getMilestoneSummary } from "../api/analyticsApi";


function PendingMilestones() {

  const [data, setData] = useState({
    totalMilestones: 0,
    pendingMilestones: 0,
    completedMilestones: 0,
    overdueMilestones: 0
  });


  useEffect(() => {

    getMilestoneSummary()
      .then((response) => {
        setData(response.data);
      })
      .catch((error) => {
        console.log(error);
      });

  }, []);


  return (

    <Grid container spacing={2}>

      <Grid item xs={12} md={3}>
        <Card>
          <CardContent>
            <Typography>
              Total Milestones
            </Typography>

            <Typography variant="h5">
              {data.totalMilestones}
            </Typography>
          </CardContent>
        </Card>
      </Grid>


      <Grid item xs={12} md={3}>
        <Card>
          <CardContent>
            <Typography>
              Pending
            </Typography>

            <Typography variant="h5">
              {data.pendingMilestones}
            </Typography>
          </CardContent>
        </Card>
      </Grid>


      <Grid item xs={12} md={3}>
        <Card>
          <CardContent>
            <Typography>
              Completed
            </Typography>

            <Typography variant="h5">
              {data.completedMilestones}
            </Typography>
          </CardContent>
        </Card>
      </Grid>


      <Grid item xs={12} md={3}>
        <Card>
          <CardContent>
            <Typography>
              Overdue
            </Typography>

            <Typography variant="h5">
              {data.overdueMilestones}
            </Typography>
          </CardContent>
        </Card>
      </Grid>

    </Grid>

  );

}


export default PendingMilestones;