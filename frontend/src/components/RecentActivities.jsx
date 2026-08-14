import React, { useEffect, useState } from "react";
import { Typography, List, ListItem, ListItemText, Divider } from "@mui/material";
import { getRecentActivities } from "../api/analyticsApi";

function RecentActivities() {
  const [activities, setActivities] = useState([]);

  useEffect(() => {
    getRecentActivities()
      .then((res) => setActivities(res.data || []))
      .catch((err) => {
        console.log("Activities Error:", err);
        setActivities([]);
      });
  }, []);

  if (activities.length === 0) {
    return (
      <Typography sx={{ mt: 6, textAlign: "center", color: "text.secondary" }}>
        📝 No recent activities found
      </Typography>
    );
  }

  return (
    <List sx={{ maxHeight: 340, overflow: "auto" }}>
      {activities.map((item, index) => (
        <React.Fragment key={index}>
          <ListItem sx={{ borderRadius: 3, mb: 1, background: "#F8F9FF" }}>
            <ListItemText
              primary={item.message || item.activity || item.description || "No activity"}
              secondary={item.date || item.createdDate || ""}
            />
          </ListItem>
          <Divider />
        </React.Fragment>
      ))}
    </List>
  );
}

export default RecentActivities;