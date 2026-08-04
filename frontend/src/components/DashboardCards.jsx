import React from "react";
import { Card, CardContent, Grid, Typography, Avatar } from "@mui/material";
import {
  People,
  AccountBalance,
  CurrencyRupee,
  PendingActions,
} from "@mui/icons-material";

import { summaryData } from "../data/dashboardData";

function DashboardCards() {
  const cards = [
    {
      title: "Total Beneficiaries",
      value: summaryData.totalBeneficiaries,
      description: "Registered beneficiaries",
      icon: <People />,
      color: "#1976d2",
    },
    {
      title: "Total Schemes",
      value: summaryData.totalSchemes,
      description: "Active government schemes",
      icon: <AccountBalance />,
      color: "#2e7d32",
    },
    {
      title: "Funds Released",
      value: summaryData.fundsReleased,
      description: "Total amount disbursed",
      icon: <CurrencyRupee />,
      color: "#ef6c00",
    },
    {
      title: "Pending Milestones",
      value: summaryData.pendingMilestones,
      description: "Awaiting approval",
      icon: <PendingActions />,
      color: "#d32f2f",
    },
  ];

  return (
    <Grid container spacing={3}>
      {cards.map((card) => (
        <Grid item xs={12} sm={6} md={3} key={card.title}>
          <Card
            sx={{
              borderRadius: 3,
              boxShadow: 4,
              transition: "0.3s",
              "&:hover": {
                transform: "translateY(-6px)",
                boxShadow: 8,
              },
            }}
          >
            <CardContent>
              <Avatar
                sx={{
                  bgcolor: card.color,
                  width: 55,
                  height: 55,
                  mb: 2,
                }}
              >
                {card.icon}
              </Avatar>

              <Typography
                variant="subtitle2"
                color="text.secondary"
              >
                {card.title}
              </Typography>

              <Typography
                variant="h4"
                fontWeight="bold"
                sx={{ mt: 1 }}
              >
                {card.value}
              </Typography>

              <Typography
                variant="body2"
                color="text.secondary"
                sx={{ mt: 1 }}
              >
                {card.description}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}

export default DashboardCards;