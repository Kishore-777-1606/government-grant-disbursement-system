import React from "react";
import { recentActivities } from "../data/dashboardData";
import NoData from "./NoData";

import {
  Paper,
  Typography,
  List,
  ListItem,
  ListItemAvatar,
  Avatar,
  ListItemText,
  Divider,
} from "@mui/material";

import {
  CheckCircle,
  Notifications,
} from "@mui/icons-material";

function RecentActivities() {

  // Show No Data component if there are no activities
  if (recentActivities.length === 0) {
    return <NoData />;
  }

  return (
    <Paper
      elevation={4}
      sx={{
        p: 3,
        mt: 4,
        borderRadius: 3,
      }}
    >
      <Typography
        variant="h5"
        fontWeight="bold"
        mb={3}
      >
        📝 Recent Activities
      </Typography>

      <List>
        {recentActivities.map((activity, index) => (
          <React.Fragment key={activity.id}>
            <ListItem>
              <ListItemAvatar>
                <Avatar
                  sx={{
                    bgcolor: "#1976d2",
                  }}
                >
                  <CheckCircle />
                </Avatar>
              </ListItemAvatar>

              <ListItemText
                primary={activity.activity}
                secondary="Recently Updated"
              />
            </ListItem>

            {index !== recentActivities.length - 1 && (
              <Divider />
            )}
          </React.Fragment>
        ))}
      </List>
    </Paper>
  );
}

export default RecentActivities;