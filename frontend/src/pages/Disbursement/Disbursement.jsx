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
    markMilestoneInProgress,
    markMilestoneNonCompliant,
    getMilestoneReminders
} from "../../services/disbursementService";

function statusColor(status) {

    if (!status) return "default";

    switch (status.toLowerCase()) {

        case "released":
        case "completed":
            return "success";

        case "overdue":
        case "non-compliant":
            return "error";

        case "in progress":
            return "info";

        case "scheduled":
        case "pending":
        default:
            return "warning";

    }

}

function Disbursement() {

    const stored = localStorage.getItem("user");
    const currentRole = stored ? JSON.parse(stored)?.role : null;

    // Mirrors the backend's @PreAuthorize on ComplianceMilestoneController
    // exactly — a role that can't call the endpoint shouldn't see the button.
    const canMarkComplete =
        ["FIELD_OFFICER", "DISTRICT_OFFICER", "ADMIN"].includes(currentRole);

    const canMarkInProgress =
        ["FIELD_OFFICER", "DISTRICT_OFFICER", "ADMIN"].includes(currentRole);

    const canMarkNonCompliant =
        ["DISTRICT_OFFICER", "ADMIN"].includes(currentRole);

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

    const handleInProgress = async (milestoneId) => {

        try {

            await markMilestoneInProgress(milestoneId);
            loadData();

        } catch (err) {

            console.error(err);
            alert(
                err?.response?.data?.message ||
                "Could not mark this milestone in progress."
            );

        }

    };

    const handleNonCompliant = async (milestoneId) => {

        const reason = window.prompt(
            "Reason for marking this milestone non-compliant (optional):"
        );

        // User clicked Cancel — abort, don't submit anything.
        if (reason === null) {
            return;
        }

        try {

            await markMilestoneNonCompliant(milestoneId, reason.trim() || undefined);
            loadData();

        } catch (err) {

            console.error(err);
            alert(
                err?.response?.data?.message ||
                "Could not mark this milestone non-compliant."
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
                "Could not release this installment. Please try again."
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
                                const milestoneStatus =
                                    milestone?.status?.toLowerCase() || "";

                                const isCompleted = milestoneStatus === "completed";
                                const isNonCompliant = milestoneStatus === "non-compliant";
                                const isInProgress = milestoneStatus === "in progress";

                                const milestoneDone = isCompleted;
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

                                            <Box display="flex" gap={1} flexWrap="wrap">

                                                {milestone && canMarkInProgress &&
                                                    !isCompleted && !isNonCompliant && !isInProgress && (

                                                    <Button
                                                        variant="outlined"
                                                        size="small"
                                                        onClick={() =>
                                                            handleInProgress(
                                                                milestone.milestoneId
                                                            )
                                                        }
                                                    >
                                                        Mark In Progress
                                                    </Button>

                                                )}

                                                {milestone && canMarkComplete &&
                                                    !milestoneDone && !isNonCompliant && (

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

                                                {milestone && canMarkNonCompliant &&
                                                    !isCompleted && !isNonCompliant && (

                                                    <Button
                                                        variant="outlined"
                                                        color="error"
                                                        size="small"
                                                        onClick={() =>
                                                            handleNonCompliant(
                                                                milestone.milestoneId
                                                            )
                                                        }
                                                    >
                                                        Mark Non-Compliant
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