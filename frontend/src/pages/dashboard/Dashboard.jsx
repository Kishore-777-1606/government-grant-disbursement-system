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
  Stack,
} from "@mui/material";

import {
  People,
  Assignment,
  PendingActions,
  CheckCircle,
  TrendingUp,
} from "@mui/icons-material";

import MainLayout from "../../layouts/MainLayout";

const stats = [
  {
    title: "Beneficiaries",
    value: 124,
    icon: <People sx={{ fontSize: 28 }} />,
    color: "#2563EB",
    bgLight: "#EFF6FF",
  },
  {
    title: "Applications",
    value: 86,
    icon: <Assignment sx={{ fontSize: 28 }} />,
    color: "#7C3AED",
    bgLight: "#F5F3FF",
  },
  {
    title: "Pending",
    value: 15,
    icon: <PendingActions sx={{ fontSize: 28 }} />,
    color: "#EA580C",
    bgLight: "#FFF7ED",
  },
  {
    title: "Approved",
    value: 71,
    icon: <CheckCircle sx={{ fontSize: 28 }} />,
    color: "#16A34A",
    bgLight: "#F0FDF4",
  },
];

const applications = [
  { id: 101, name: "Rahul Sharma", scheme: "Farmer Subsidy", status: "Pending" },
  { id: 102, name: "Priya Singh", scheme: "Education Grant", status: "Approved" },
  { id: 103, name: "Amit Verma", scheme: "Housing Scheme", status: "Pending" },
];

function Dashboard() {
  return (
    <MainLayout>
      <Box>
        {/* Welcome Banner */}
        <Paper
          elevation={0}
          sx={{
            p: { xs: 3, md: 4 },
            mb: 4,
            borderRadius: 3,
            background: "linear-gradient(135deg, #1E40AF 0%, #3B82F6 100%)",
            color: "white",
          }}
        >
          <Typography variant="h4" fontWeight={700}>
            Welcome, Admin!
          </Typography>
          <Typography sx={{ mt: 0.5, opacity: 0.85, fontSize: 15 }}>
            Government Grant Disbursement Tracking System
          </Typography>
        </Paper>

        {/* Stat Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {stats.map((item) => (
            <Grid item xs={12} sm={6} md={3} key={item.title}>
              <Paper
                elevation={0}
                sx={{
                  p: 3,
                  borderRadius: 3,
                  border: "1px solid #EDF0F5",
                  transition: "all 0.2s ease",
                  cursor: "pointer",
                  "&:hover": {
                    boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
                    transform: "translateY(-3px)",
                  },
                }}
              >
                <Stack direction="row" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography
                      sx={{
                        fontSize: 13,
                        fontWeight: 600,
                        color: "#64748B",
                        textTransform: "uppercase",
                        letterSpacing: 0.5,
                        mb: 1,
                      }}
                    >
                      {item.title}
                    </Typography>
                    <Typography variant="h4" fontWeight={700} sx={{ color: "#1E293B" }}>
                      {item.value}
                    </Typography>
                  </Box>
                  <Avatar
                    sx={{
                      bgcolor: item.bgLight,
                      color: item.color,
                      width: 52,
                      height: 52,
                    }}
                  >
                    {item.icon}
                  </Avatar>
                </Stack>
              </Paper>
            </Grid>
          ))}
        </Grid>

        {/* Recent Applications */}
        <Paper
          elevation={0}
          sx={{
            borderRadius: 3,
            border: "1px solid #EDF0F5",
            overflow: "hidden",
          }}
        >
          <Box sx={{ p: 3, pb: 2 }}>
            <Stack direction="row" alignItems="center" spacing={1}>
              <TrendingUp sx={{ color: "#3B82F6", fontSize: 22 }} />
              <Typography variant="h6" fontWeight={700} sx={{ color: "#1E293B" }}>
                Recent Applications
              </Typography>
            </Stack>
          </Box>

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow sx={{ background: "#F8FAFC" }}>
                  <TableCell sx={{ color: "#64748B", fontWeight: 600, fontSize: 13, textTransform: "uppercase", letterSpacing: 0.3, border: "none" }}>
                    ID
                  </TableCell>
                  <TableCell sx={{ color: "#64748B", fontWeight: 600, fontSize: 13, textTransform: "uppercase", letterSpacing: 0.3, border: "none" }}>
                    Applicant
                  </TableCell>
                  <TableCell sx={{ color: "#64748B", fontWeight: 600, fontSize: 13, textTransform: "uppercase", letterSpacing: 0.3, border: "none" }}>
                    Scheme
                  </TableCell>
                  <TableCell sx={{ color: "#64748B", fontWeight: 600, fontSize: 13, textTransform: "uppercase", letterSpacing: 0.3, border: "none" }}>
                    Status
                  </TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {applications.map((row) => (
                  <TableRow
                    key={row.id}
                    hover
                    sx={{
                      "&:last-child td": { border: 0 },
                      "& td": { borderColor: "#F1F5F9", py: 2 },
                    }}
                  >
                    <TableCell sx={{ color: "#94A3B8", fontWeight: 500 }}>#{row.id}</TableCell>
                    <TableCell sx={{ fontWeight: 600, color: "#1E293B" }}>{row.name}</TableCell>
                    <TableCell sx={{ color: "#475569" }}>{row.scheme}</TableCell>
                    <TableCell>
                      <Chip
                        label={row.status}
                        size="small"
                        sx={{
                          fontWeight: 600,
                          fontSize: 12,
                          borderRadius: 1.5,
                          bgcolor: row.status === "Approved" ? "#F0FDF4" : "#FFF7ED",
                          color: row.status === "Approved" ? "#16A34A" : "#EA580C",
                        }}
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      </Box>
    </MainLayout>
  );
}

export default Dashboard;