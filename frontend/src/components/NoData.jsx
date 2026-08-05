import React from "react";
import { Box, Typography } from "@mui/material";
import InboxIcon from "@mui/icons-material/Inbox";

function NoData() {
  return (
    <Box
      sx={{
        textAlign: "center",
        padding: "40px",
      }}
    >
      <InboxIcon
        sx={{
          fontSize: 70,
          color: "#9e9e9e",
          mb: 2,
        }}
      />

      <Typography
        variant="h6"
        color="text.secondary"
      >
        No Analytics Data Available
      </Typography>

      <Typography
        variant="body2"
        color="text.secondary"
        sx={{ mt: 1 }}
      >
        Data will be displayed here once available.
      </Typography>
    </Box>
  );
}

export default NoData;