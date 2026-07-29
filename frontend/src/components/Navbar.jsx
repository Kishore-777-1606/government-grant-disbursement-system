import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Avatar,
} from "@mui/material";

function Navbar() {
  return (
    <AppBar
      position="fixed"
      sx={{
        width: "calc(100% - 260px)",
        ml: "260px",
        background: "#1565C0",
      }}
    >
      <Toolbar
        sx={{
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <Typography variant="h6" fontWeight="bold">
          Government Grant Disbursement Tracking System
        </Typography>

        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 2,
          }}
        >
          <Typography>Welcome, Admin</Typography>

          <Avatar sx={{ bgcolor: "#0D47A1" }}>
            N
          </Avatar>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar;