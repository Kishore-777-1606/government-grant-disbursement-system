import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Avatar,
} from "@mui/material";

import { useAuth } from "../auth/useAuth";

function Navbar() {
  const {
    username,
    role,
  } = useAuth();

  const displayName = username || "User";

  const displayRole = role
    ? role
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, (letter) =>
          letter.toUpperCase()
        )
    : "";

  const avatarLetter = displayName.charAt(0).toUpperCase();

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
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar;