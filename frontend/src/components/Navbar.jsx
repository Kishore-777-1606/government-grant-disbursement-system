import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Avatar,
  Button,
} from "@mui/material";
import LogoutIcon from "@mui/icons-material/Logout";
import { useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();

  const stored = localStorage.getItem("user");
  const user = stored ? JSON.parse(stored) : null;

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  // Prefer full name; fall back to username, then a generic label if
  // somehow rendered with no stored user (shouldn't happen behind ProtectedRoute).
  const displayName = user?.fullName || user?.username || "User";
  const initial = displayName.charAt(0).toUpperCase();

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
          <Typography>Welcome, {displayName}</Typography>

          <Avatar sx={{ bgcolor: "#0D47A1" }}>{initial}</Avatar>

          <Button
            onClick={handleLogout}
            startIcon={<LogoutIcon />}
            sx={{
              color: "white",
              borderColor: "rgba(255,255,255,0.5)",
              textTransform: "none",
            }}
            variant="outlined"
            size="small"
          >
            Logout
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar;