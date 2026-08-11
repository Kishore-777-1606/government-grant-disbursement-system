import {
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Box,
} from "@mui/material";
import {
  Dashboard,
  People,
  Description,
  Assignment,
  Verified,
  CheckCircle,
  AccountTree,
  AccountBalanceWallet,
  Assessment,
  Payments,
} from "@mui/icons-material";

import { Link, useLocation } from "react-router-dom";

const drawerWidth = 260;

const menuItems = [
  { text: "Dashboard", icon: <Dashboard />, path: "/dashboard" },
  { text: "Beneficiaries", icon: <People />, path: "/beneficiaries" },
  { text: "Schemes", icon: <Description />, path: "/schemes" },
  { text: "Applications", icon: <Assignment />, path: "/applications" },
  { text: "Eligibility", icon: <Verified />, path: "/eligibility" },
  { text: "Verification", icon: <CheckCircle />, path: "/verification" },
  {
    text: "Finance Approval",
    icon: <AccountBalanceWallet />,
    path: "/finance",
  },
  { text: "Disbursement", icon: <Payments />, path: "/disbursement" },
  { text: "Status Tracking", icon: <AccountTree />, path: "/status" },
  { text: "Analytics", icon: <Assessment />, path: "/analytics" },
];

function Sidebar() {
  const location = useLocation();

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,

        "& .MuiDrawer-paper": {
          width: drawerWidth,
          background: "#0D47A1",
          color: "white",
          borderRight: "none",
        },
      }}
    >
      <Toolbar />

      <Box sx={{ textAlign: "center", py: 2 }}>
        <Typography variant="h5" fontWeight="bold">
          🏛 GrantFlow
        </Typography>

        <Typography variant="body2" sx={{ opacity: 0.8 }}>
          Government Portal
        </Typography>
      </Box>

      <List sx={{ px: 2 }}>
        {menuItems.map((item) => (
          <ListItemButton
            key={item.text}
            component={Link}
            to={item.path}
            selected={location.pathname === item.path}
            sx={{
              borderRadius: 3,
              mb: 1,
              color: "white",

              "&.Mui-selected": {
                background: "#1565C0",
              },

              "&.Mui-selected:hover": {
                background: "#1976D2",
              },

              "&:hover": {
                background: "#1976D2",
              },
            }}
          >
            <ListItemIcon sx={{ color: "white" }}>
              {item.icon}
            </ListItemIcon>

            <ListItemText primary={item.text} />
          </ListItemButton>
        ))}
      </List>
    </Drawer>
  );
}

export default Sidebar;