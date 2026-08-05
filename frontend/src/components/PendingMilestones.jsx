import React from "react";
import { pendingMilestones } from "../data/dashboardData";
import NoData from "./NoData";

import {
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
} from "@mui/material";

function PendingMilestones() {

  // Show No Data component if there are no milestones
  if (pendingMilestones.length === 0) {
    return <NoData />;
  }

  return (
    <Paper
      elevation={4}
      sx={{
        p: 3,
        mt: 4,
        borderRadius: 3,
      }}
    >
      <Typography
        variant="h5"
        fontWeight="bold"
        mb={3}
      >
        📋 Pending Milestones
      </Typography>

      <TableContainer>
        <Table>
          <TableHead>
            <TableRow
              sx={{
                backgroundColor: "#1976d2",
              }}
            >
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                Beneficiary
              </TableCell>

              <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                Scheme
              </TableCell>

              <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                Due Date
              </TableCell>

              <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                Status
              </TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {pendingMilestones.map((item, index) => (
              <TableRow
                key={item.id}
                hover
                sx={{
                  backgroundColor:
                    index % 2 === 0 ? "#fafafa" : "#ffffff",
                }}
              >
                <TableCell>{item.beneficiary}</TableCell>
                <TableCell>{item.scheme}</TableCell>
                <TableCell>{item.dueDate}</TableCell>

                <TableCell>
                  <Chip
                    label={item.status}
                    color={
                      item.status === "Completed"
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
  );
}

export default PendingMilestones;