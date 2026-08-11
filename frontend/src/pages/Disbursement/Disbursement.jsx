import { useEffect, useState } from "react";
import {
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    CircularProgress,
    Button,
    Chip,
    Box,
    Alert
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";

import {
    getAllInstallments,
    releaseInstallment,
    completeMilestone,
    getMilestoneReminders
} from "../../services/disbursementService";

function statusColor(status) {

    if (!status) return "default";

    switch (status.toLowerCase()) {

        case "released":
        case "completed":
            return "success";

        case "overdue":
            return "error";

        case "scheduled":
        case "pending":
        default:
            return "warning";

    }

}

function Disbursement() {

    const [installments, setInstallments] = useState([]);
    const [reminders, setReminders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {

        try {

            const [installmentData, reminderData] = await Promise.all([
                getAllInstallments(),
                getMilestoneReminders()
            ]);

            setInstallments(installmentData);
            setReminders(reminderData);

        } catch (err) {

            console.error(err);

        } finally {

            setLoading(false);

        }

    };

    const handleCompleteMilestone = async (milestoneId) => {

        try {

            await completeMilestone(milestoneId);
            loadData();

        } catch (err) {

            console.error(err);
            alert(
                err?.response?.data?.message ||
                "Could not complete this milestone."
            );

        }

    };

    const handleRelease = async (installmentId) => {

        try {

            await releaseInstallment(installmentId);
            loadData();

        } catch (err) {

            console.error(err);
            alert(
                err?.response?.data?.message ||
                "Milestone not yet completed — cannot release this installment."
            );

        }

    };

    return (

        <MainLayout>

            <Typography variant="h4" gutterBottom>
                Staged Disbursement & Compliance Milestones
            </Typography>

            {reminders.length > 0 && (

                <Alert severity="warning" sx={{ mb: 2 }}>
                    {reminders.length} milestone(s) due within the next 3 days.
                </Alert>

            )}

            {loading ? (

                <CircularProgress />

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell><b>Installment</b></TableCell>
                                <TableCell><b>Plan ID</b></TableCell>
                                <TableCell><b>Amount</b></TableCell>
                                <TableCell><b>Scheduled Date</b></TableCell>
                                <TableCell><b>Milestone</b></TableCell>
                                <TableCell><b>Milestone Status</b></TableCell>
                                <TableCell><b>Installment Status</b></TableCell>
                                <TableCell><b>Action</b></TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {installments.map((inst) => {

                                const milestone = inst.milestone;
                                const milestoneDone =
                                    milestone &&
                                    milestone.status &&
                                    milestone.status.toLowerCase() === "completed";
                                const alreadyReleased =
                                    inst.status &&
                                    inst.status.toLowerCase() === "released";

                                return (

                                    <TableRow key={inst.installmentId} hover>

                                        <TableCell>
                                            #{inst.installmentNumber}
                                        </TableCell>

                                        <TableCell>
                                            {inst.disbursementPlan?.planId}
                                        </TableCell>

                                        <TableCell>
                                            {inst.installmentAmount}
                                        </TableCell>

                                        <TableCell>
                                            {inst.scheduledDate}
                                        </TableCell>

                                        <TableCell>
                                            {milestone?.milestoneType || "-"}
                                        </TableCell>

                                        <TableCell>

                                            <Chip
                                                size="small"
                                                label={milestone?.status || "-"}
                                                color={statusColor(milestone?.status)}
                                            />

                                        </TableCell>

                                        <TableCell>

                                            <Chip
                                                size="small"
                                                label={inst.status}
                                                color={statusColor(inst.status)}
                                            />

                                        </TableCell>

                                        <TableCell>

                                            <Box display="flex" gap={1}>

                                                {!milestoneDone && milestone && (

                                                    <Button
                                                        variant="outlined"
                                                        size="small"
                                                        onClick={() =>
                                                            handleCompleteMilestone(
                                                                milestone.milestoneId
                                                            )
                                                        }
                                                    >
                                                        Mark Milestone Complete
                                                    </Button>

                                                )}

                                                {!alreadyReleased && (

                                                    <Button
                                                        variant="contained"
                                                        color="success"
                                                        size="small"
                                                        disabled={!milestoneDone}
                                                        onClick={() =>
                                                            handleRelease(
                                                                inst.installmentId
                                                            )
                                                        }
                                                    >
                                                        Release Fund
                                                    </Button>

                                                )}

                                            </Box>

                                        </TableCell>

                                    </TableRow>

                                );

                            })}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

        </MainLayout>

    );

}

export default Disbursement;