import React from "react";
import { Box, CircularProgress, Typography } from "@mui/material";

function Loading() {
  return (
    <Box
      sx={{
        height: "60vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        flexDirection: "column",
      }}
    >
      <CircularProgress size={50} />

      <Typography
        variant="h6"
        sx={{ mt: 2 }}
      >
        Loading Analytics Dashboard...
      </Typography>
    </Box>
  );
}

export default Loading;