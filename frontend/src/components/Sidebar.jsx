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
  History,
} from "@mui/icons-material";

import { Link, useLocation } from "react-router-dom";

const drawerWidth = 260;

// Mirrors the @PreAuthorize rules on the backend controllers — a role that
// can't call the underlying APIs shouldn't see the menu item either.
const menuItems = [
  { text: "Dashboard", icon: <Dashboard />, path: "/dashboard", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Beneficiaries", icon: <People />, path: "/beneficiaries", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Schemes", icon: <Description />, path: "/schemes", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Applications", icon: <Assignment />, path: "/applications", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Eligibility", icon: <Verified />, path: "/eligibility", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Verification", icon: <CheckCircle />, path: "/verification", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Finance Approval", icon: <AccountBalanceWallet />, path: "/finance", roles: ["DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Disbursement", icon: <Payments />, path: "/disbursement", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Status Tracking", icon: <AccountTree />, path: "/status", roles: ["FIELD_OFFICER", "DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Analytics", icon: <Assessment />, path: "/analytics", roles: ["DISTRICT_OFFICER", "FINANCE_APPROVER", "ADMIN"] },
  { text: "Audit Log", icon: <History />, path: "/audit-log", roles: ["ADMIN", "DISTRICT_OFFICER"] },

];

function Sidebar() {
  const location = useLocation();

  const stored = localStorage.getItem("user");
  const currentRole = stored ? JSON.parse(stored)?.role : null;

  const visibleItems = menuItems.filter((item) =>
    currentRole ? item.roles.includes(currentRole) : false
  );

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
        {visibleItems.map((item) => (
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