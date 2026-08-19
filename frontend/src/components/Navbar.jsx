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

import { useAuth } from "../auth/useAuth";

function Navbar() {
  const navigate = useNavigate();

  const {
    username,
    role,
    logout,
  } = useAuth();

  const displayName =
    username || "User";

  const displayRole = role
    ? role
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, (letter) =>
          letter.toUpperCase()
        )
    : "";

  /*
   * Get first letter for Avatar
   */
  const avatarLetter =
    displayName.charAt(0).toUpperCase();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

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
        {/* Application title */}
        <Typography
          variant="h6"
          fontWeight="bold"
        >
          Government Grant Disbursement
          Tracking System
        </Typography>

        {/* User information */}
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 2,
          }}
        >
          <Box
            sx={{
              textAlign: "right",
            }}
          >
            <Typography
              variant="body2"
              fontWeight="bold"
            >
              Welcome, {displayName}
            </Typography>

            {displayRole && (
              <Typography
                variant="caption"
                sx={{
                  opacity: 0.85,
                }}
              >
                {displayRole}
              </Typography>
            )}
          </Box>

          <Avatar
            sx={{
              bgcolor: "#0D47A1",
              fontWeight: "bold",
            }}
          >
            {avatarLetter}
          </Avatar>

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