import {
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Box,
  Divider,
  Button,
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
  Logout,
} from "@mui/icons-material";

import {
  Link,
  useLocation,
  useNavigate,
} from "react-router-dom";

import { useAuth } from "../auth/useAuth";

const drawerWidth = 260;

/*
 * Role-based menu configuration
 */
const menuItems = [
  {
    text: "Dashboard",
    icon: <Dashboard />,
    path: "/dashboard",
    roles: [
      "FIELD_OFFICER",
      "DISTRICT_OFFICER",
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Beneficiaries",
    icon: <People />,
    path: "/beneficiaries",
    roles: [
      "FIELD_OFFICER",
      "DISTRICT_OFFICER",
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Schemes",
    icon: <Description />,
    path: "/schemes",
    roles: [
      "DISTRICT_OFFICER",
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Applications",
    icon: <Assignment />,
    path: "/applications",
    roles: [
      "FIELD_OFFICER",
      "DISTRICT_OFFICER",
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Eligibility",
    icon: <Verified />,
    path: "/eligibility",
    roles: [
      "FIELD_OFFICER",
      "DISTRICT_OFFICER",
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Verification",
    icon: <CheckCircle />,
    path: "/verification",
    roles: [
      "FIELD_OFFICER",
      "DISTRICT_OFFICER",
      "ADMIN",
    ],
  },

  {
    text: "Finance Approval",
    icon: <AccountBalanceWallet />,
    path: "/finance",
    roles: [
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Disbursement",
    icon: <Payments />,
    path: "/disbursement",
    roles: [
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Status Tracking",
    icon: <AccountTree />,
    path: "/status",
    roles: [
      "FIELD_OFFICER",
      "DISTRICT_OFFICER",
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },

  {
    text: "Analytics",
    icon: <Assessment />,
    path: "/analytics",
    roles: [
      "FINANCE_APPROVER",
      "ADMIN",
    ],
  },
];

function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  const {
    role,
    username,
    logout,
  } = useAuth();

  /*
   * Normalize role so that
   * FIELD_OFFICER and field_officer
   * are treated consistently.
   */
  const currentRole = role?.toUpperCase();

  /*
   * Show only menu items allowed
   * for the current user's role.
   */
  const visibleMenuItems = menuItems.filter(
    (item) =>
      item.roles.includes(currentRole)
  );

  /*
   * Logout handler
   */
  const handleLogout = () => {
    logout();

    navigate("/login", {
      replace: true,
    });
  };

  /*
   * Convert role into readable text.
   *
   * Example:
   * FINANCE_APPROVER
   * becomes
   * Finance Approver
   */
  const formattedRole = currentRole
    ? currentRole
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, (letter) =>
          letter.toUpperCase()
        )
    : "";

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
          boxSizing: "border-box",
        },
      }}
    >
      {/* Space for Navbar */}
      <Toolbar />

      {/* =========================
          LOGO / USER INFORMATION
         ========================= */}

      <Box
        sx={{
          textAlign: "center",
          py: 2,
          px: 2,
        }}
      >
        <Typography
          variant="h5"
          fontWeight="bold"
        >
          🏛 GrantFlow
        </Typography>

        <Typography
          variant="body2"
          sx={{
            opacity: 0.8,
            mt: 0.5,
          }}
        >
          Government Portal
        </Typography>

        {/* Username */}
        {username && (
          <Typography
            variant="body2"
            sx={{
              mt: 2,
              fontWeight: "bold",
            }}
          >
            {username}
          </Typography>
        )}

        {/* Role */}
        {formattedRole && (
          <Typography
            variant="caption"
            sx={{
              display: "block",
              mt: 0.5,
              opacity: 0.8,
            }}
          >
            {formattedRole}
          </Typography>
        )}
      </Box>

      <Divider
        sx={{
          borderColor:
            "rgba(255,255,255,0.2)",
          mx: 2,
        }}
      />

      {/* =========================
          NAVIGATION MENU
         ========================= */}

      <List
        sx={{
          px: 2,
          py: 2,
          flexGrow: 1,
        }}
      >
        {visibleMenuItems.map((item) => (
          <ListItemButton
            key={item.text}
            component={Link}
            to={item.path}
            selected={
              location.pathname === item.path
            }
            sx={{
              borderRadius: 3,
              mb: 1,
              color: "white",

              "& .MuiListItemIcon-root": {
                color: "white",
                minWidth: 42,
              },

              "&.Mui-selected": {
                backgroundColor: "#1565C0",
              },

              "&.Mui-selected:hover": {
                backgroundColor: "#1976D2",
              },

              "&:hover": {
                backgroundColor: "#1976D2",
              },
            }}
          >
            <ListItemIcon>
              {item.icon}
            </ListItemIcon>

            <ListItemText
              primary={item.text}
            />
          </ListItemButton>
        ))}
      </List>

      {/* =========================
          LOGOUT
         ========================= */}

      <Box
        sx={{
          p: 2,
        }}
      >
        <Button
          fullWidth
          variant="outlined"
          startIcon={<Logout />}
          onClick={handleLogout}
          sx={{
            color: "white",
            borderColor:
              "rgba(255,255,255,0.5)",
            borderRadius: 2,
            py: 1,

            "&:hover": {
              borderColor: "white",
              backgroundColor:
                "rgba(255,255,255,0.1)",
            },
          }}
        >
          Logout
        </Button>
      </Box>
    </Drawer>
  );
}

export default Sidebar;