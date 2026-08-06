import { useEffect, useState } from "react";

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
import { getDashboardSummary } from "../../api/analyticsApi";


function Dashboard() {


  const [summary, setSummary] = useState({
    totalBeneficiaries: 0,
    totalSchemes: 0,
    totalApplications: 0,
    activeDisbursementPlans: 0,
    pendingMilestones: 0,
    releasedInstallments: 0,
  });


  useEffect(() => {

    getDashboardSummary()
      .then((response) => {

        console.log(response.data);

        setSummary(response.data);

      })
      .catch((error) => {

        console.log("Dashboard API Error:", error);

      });

  }, []);



  const stats = [

    {
      title: "Beneficiaries",
      value: summary.totalBeneficiaries,
      icon: <People sx={{ fontSize: 28 }} />,
      color: "#2563EB",
      bgLight: "#EFF6FF",
    },

    {
      title: "Applications",
      value: summary.totalApplications,
      icon: <Assignment sx={{ fontSize: 28 }} />,
      color: "#7C3AED",
      bgLight: "#F5F3FF",
    },

    {
      title: "Pending Milestones",
      value: summary.pendingMilestones,
      icon: <PendingActions sx={{ fontSize: 28 }} />,
      color: "#EA580C",
      bgLight: "#FFF7ED",
    },

    {
      title: "Released Installments",
      value: summary.releasedInstallments,
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
            background:
              "linear-gradient(135deg, #1E40AF 0%, #3B82F6 100%)",
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
                }}
              >

                <Stack
                  direction="row"
                  alignItems="center"
                  justifyContent="space-between"
                >

                  <Box>

                    <Typography
                      sx={{
                        fontSize:13,
                        fontWeight:600,
                        color:"#64748B",
                        textTransform:"uppercase",
                        mb:1
                      }}
                    >
                      {item.title}
                    </Typography>


                    <Typography
                      variant="h4"
                      fontWeight={700}
                    >
                      {item.value}
                    </Typography>


                  </Box>


                  <Avatar
                    sx={{
                      bgcolor:item.bgLight,
                      color:item.color,
                      width:52,
                      height:52
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
            borderRadius:3,
            border:"1px solid #EDF0F5",
            overflow:"hidden"
          }}
        >

          <Box sx={{p:3}}>

            <Stack direction="row" spacing={1} alignItems="center">

              <TrendingUp sx={{color:"#3B82F6"}}/>

              <Typography variant="h6" fontWeight={700}>
                Recent Applications
              </Typography>

            </Stack>

          </Box>


          <TableContainer>

            <Table>

              <TableHead>

                <TableRow>

                  <TableCell>ID</TableCell>
                  <TableCell>Applicant</TableCell>
                  <TableCell>Scheme</TableCell>
                  <TableCell>Status</TableCell>

                </TableRow>

              </TableHead>



              <TableBody>

                {applications.map((row)=>(

                  <TableRow key={row.id}>

                    <TableCell>
                      #{row.id}
                    </TableCell>

                    <TableCell>
                      {row.name}
                    </TableCell>

                    <TableCell>
                      {row.scheme}
                    </TableCell>


                    <TableCell>

                      <Chip
                        label={row.status}
                        size="small"
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