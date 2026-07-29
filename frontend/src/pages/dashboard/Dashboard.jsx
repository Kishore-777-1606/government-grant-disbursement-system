import {
  Grid,
  Paper,
  Typography,
  Avatar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Box,
} from "@mui/material";

import {
  People,
  Assignment,
  PendingActions,
  CheckCircle,
} from "@mui/icons-material";

const stats = [
  {
    title: "Beneficiaries",
    value: 124,
    icon: <People sx={{ fontSize: 35 }} />,
    color: "#1976D2",
  },
  {
    title: "Applications",
    value: 86,
    icon: <Assignment sx={{ fontSize: 35 }} />,
    color: "#7B1FA2",
  },
  {
    title: "Pending",
    value: 15,
    icon: <PendingActions sx={{ fontSize: 35 }} />,
    color: "#F57C00",
  },
  {
    title: "Approved",
    value: 71,
    icon: <CheckCircle sx={{ fontSize: 35 }} />,
    color: "#2E7D32",
  },
];

const applications = [
  {
    id: 101,
    name: "Rahul Sharma",
    scheme: "Farmer Subsidy",
    status: "Pending",
  },
  {
    id: 102,
    name: "Priya Singh",
    scheme: "Education Grant",
    status: "Approved",
  },
  {
    id: 103,
    name: "Amit Verma",
    scheme: "Housing Scheme",
    status: "Pending",
  },
];

function Dashboard() {
  return (
    <Box>

      {/* Welcome Banner */}

      <Paper
        sx={{
          p: 4,
          mb: 4,
          borderRadius: 4,
          background: "linear-gradient(90deg,#1565C0,#42A5F5)",
          color: "white",
        }}
      >
        <Typography variant="h4" fontWeight="bold">
          Welcome, Admin 👋
        </Typography>

        <Typography sx={{ mt: 1 }}>
          Government Grant Disbursement Tracking System
        </Typography>
      </Paper>

      {/* Cards */}

      <Grid container spacing={3} mb={4}>
        {stats.map((item) => (
          <Grid item xs={12} sm={6} md={3} key={item.title}>
            <Paper
              elevation={5}
              sx={{
                p: 3,
                borderRadius: 4,
                transition: "0.3s",
                cursor: "pointer",

                "&:hover": {
                  transform: "translateY(-6px)",
                },
              }}
            >
              <Avatar
                sx={{
                  bgcolor: item.color,
                  width: 60,
                  height: 60,
                  mb: 2,
                }}
              >
                {item.icon}
              </Avatar>

              <Typography color="text.secondary">
                {item.title}
              </Typography>

              <Typography variant="h4" fontWeight="bold">
                {item.value}
              </Typography>
            </Paper>
          </Grid>
        ))}
      </Grid>

      {/* Recent Applications */}

      <Paper
        elevation={3}
        sx={{
          p: 3,
          borderRadius: 4,
        }}
      >
        <Typography variant="h5" fontWeight="bold" mb={3}>
          Recent Applications
        </Typography>

        <TableContainer>
          <Table>

            <TableHead>

              <TableRow
                sx={{
                  background: "#1565C0",
                }}
              >
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  ID
                </TableCell>

                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Applicant
                </TableCell>

                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Scheme
                </TableCell>

                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Status
                </TableCell>

              </TableRow>

            </TableHead>

            <TableBody>

              {applications.map((row) => (

                <TableRow
                  key={row.id}
                  hover
                >

                  <TableCell>{row.id}</TableCell>

                  <TableCell>{row.name}</TableCell>

                  <TableCell>{row.scheme}</TableCell>

                  <TableCell>

                    <Chip
                      label={row.status}
                      color={
                        row.status === "Approved"
                          ? "success"
                          : "warning"
                      }
                    />

                  </TableCell>

                </TableRow>

              ))}

            </TableBody>

          </Table>
        </TableContainer>

      </Paper>

    </Box>
  );
}

export default Dashboard;