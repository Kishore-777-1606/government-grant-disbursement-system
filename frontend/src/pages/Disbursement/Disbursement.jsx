import { useCallback, useEffect, useState } from "react";

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
    Alert,
    Snackbar,
    Divider,
    TextField,
    MenuItem
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";

import {
    getAllPlans,
    createDisbursementPlan,
    getAllInstallments,
    releaseInstallment,
    completeMilestone,
    markMilestoneInProgress,
    markMilestoneNonCompliant,
    getMilestoneReminders,
    checkOverdueMilestones
} from "../../services/disbursementService";

import {
    getAllApplications
} from "../../services/applicationService";


// =====================================================
// STATUS COLOR
// =====================================================

const statusColor = (status) => {
    if (!status) {
        return "default";
    }

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
            return "warning";

        case "active":
            return "info";

        case "cancelled":
            return "error";

        default:
            return "default";
    }
};


// =====================================================
// DISBURSEMENT COMPONENT
// =====================================================

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

    // =================================================
    // STATE
    // =================================================

    const [plans, setPlans] = useState([]);
    const [installments, setInstallments] = useState([]);
    const [reminders, setReminders] = useState([]);
    const [applications, setApplications] = useState([]);

    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false);
    const [creatingPlan, setCreatingPlan] = useState(false);

    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    // =================================================
    // CREATE PLAN FORM STATE
    // =================================================

    const [applicationId, setApplicationId] = useState("");
    const [totalAmount, setTotalAmount] = useState("");
    const [numberOfInstallments, setNumberOfInstallments] =
        useState("");


    // =================================================
    // LOAD DATA
    // =================================================

    const loadData = useCallback(async () => {

        try {

            setLoading(true);
            setError("");

            // -----------------------------------------
            // CHECK OVERDUE MILESTONES
            // -----------------------------------------

            try {

                await checkOverdueMilestones();

            } catch (err) {

                console.warn(
                    "Overdue check failed:",
                    err
                );
            }


            // -----------------------------------------
            // LOAD ALL DATA
            // -----------------------------------------

            const [
                planData,
                installmentData,
                reminderData,
                applicationData
            ] = await Promise.all([
                getAllPlans(),
                getAllInstallments(),
                getMilestoneReminders(),
                getAllApplications()
            ]);


            // -----------------------------------------
            // SET PLANS
            // -----------------------------------------

            setPlans(
                Array.isArray(planData)
                    ? planData
                    : []
            );


            // -----------------------------------------
            // SET INSTALLMENTS
            // -----------------------------------------

            setInstallments(
                Array.isArray(installmentData)
                    ? installmentData
                    : []
            );


            // -----------------------------------------
            // SET REMINDERS
            // -----------------------------------------

            setReminders(
                Array.isArray(reminderData)
                    ? reminderData
                    : []
            );


            // -----------------------------------------
            // SET APPLICATIONS
            // -----------------------------------------

            setApplications(
                Array.isArray(applicationData)
                    ? applicationData
                    : []
            );

        } catch (err) {

            console.error(
                "Disbursement data loading error:",
                err
            );

            setError(
                err?.response?.data?.message ||
                err?.response?.data ||
                "Unable to load disbursement data."
            );

        } finally {

            setLoading(false);

        }

    }, []);


    // =================================================
    // INITIAL LOAD
    // =================================================
    useEffect(() => {
        const timer = setTimeout(() => {
            loadData();
        }, 0);

        return () => {
            clearTimeout(timer);
        };
    }, [loadData]);


    // =================================================
    // CREATE DISBURSEMENT PLAN
    // =================================================

    const handleCreatePlan = async (event) => {

        event.preventDefault();

        // ---------------------------------------------
        // VALIDATE APPLICATION
        // ---------------------------------------------

        if (!applicationId) {

            setError(
                "Please select an application."
            );

            return;
        }


        // ---------------------------------------------
        // VALIDATE AMOUNT
        // ---------------------------------------------

        if (
            !totalAmount ||
            Number(totalAmount) <= 0
        ) {

            setError(
                "Please enter a valid grant amount."
            );

            return;
        }


        // ---------------------------------------------
        // VALIDATE INSTALLMENTS
        // ---------------------------------------------

        if (
            !numberOfInstallments ||
            Number(numberOfInstallments) <= 0
        ) {

            setError(
                "Please enter a valid number of installments."
            );

            return;
        }


        try {

            setCreatingPlan(true);
            setError("");


            // -----------------------------------------
            // CREATE PLAN
            // -----------------------------------------

            await createDisbursementPlan(
                applicationId,
                totalAmount,
                numberOfInstallments
            );


            // -----------------------------------------
            // SUCCESS MESSAGE
            // -----------------------------------------

            setSuccessMessage(
                "Disbursement plan created successfully."
            );


            // -----------------------------------------
            // CLEAR FORM
            // -----------------------------------------

            setApplicationId("");
            setTotalAmount("");
            setNumberOfInstallments("");


            // -----------------------------------------
            // RELOAD DATA
            // -----------------------------------------

            await loadData();

        } catch (err) {

            console.error(
                "Create disbursement plan error:",
                err
            );

            setError(
                err?.response?.data?.message ||
                err?.response?.data ||
                "Could not create disbursement plan."
            );

        } finally {

            setCreatingPlan(false);

        }
    };


    // =================================================
    // COMPLETE MILESTONE
    // =================================================

    const handleCompleteMilestone = async (
        milestoneId
    ) => {

        if (!milestoneId) {

            setError(
                "Milestone ID is missing."
            );

            return;
        }

        try {

            setActionLoading(true);
            setError("");


            await completeMilestone(
                milestoneId
            );


            setSuccessMessage(
                "Milestone completed successfully."
            );


            await loadData();

        } catch (err) {

            console.error(
                "Complete milestone error:",
                err
            );

            setError(
                err?.response?.data?.message ||
                err?.response?.data ||
                "Could not complete the milestone."
            );

        } finally {

            setActionLoading(false);

        }
    };


    // =================================================
    // MARK IN PROGRESS
    // =================================================

    const handleInProgress = async (milestoneId) => {

        if (!milestoneId) {

            setError(
                "Milestone ID is missing."
            );

            return;
        }

        try {

            setActionLoading(true);
            setError("");

            await markMilestoneInProgress(milestoneId);

            setSuccessMessage(
                "Milestone marked as in progress."
            );

            await loadData();

        } catch (err) {

            console.error(
                "Mark in progress error:",
                err
            );

            setError(
                err?.response?.data?.message ||
                "Could not mark this milestone in progress."
            );

        } finally {

            setActionLoading(false);

        }

    };


    // =================================================
    // MARK NON-COMPLIANT
    // =================================================

    const handleNonCompliant = async (milestoneId) => {

        if (!milestoneId) {

            setError(
                "Milestone ID is missing."
            );

            return;
        }

        const reason = window.prompt(
            "Reason for marking this milestone non-compliant (optional):"
        );

        // User clicked Cancel — abort, don't submit anything.
        if (reason === null) {
            return;
        }

        try {

            setActionLoading(true);
            setError("");

            await markMilestoneNonCompliant(milestoneId, reason.trim() || undefined);

            setSuccessMessage(
                "Milestone marked as non-compliant."
            );

            await loadData();

        } catch (err) {

            console.error(
                "Mark non-compliant error:",
                err
            );

            setError(
                err?.response?.data?.message ||
                "Could not mark this milestone non-compliant."
            );

        } finally {

            setActionLoading(false);

        }

    };


    // =================================================
    // RELEASE FUND
    // =================================================

    const handleRelease = async (
        installmentId
    ) => {

        if (!installmentId) {

            setError(
                "Installment ID is missing."
            );

            return;
        }


        try {

            setActionLoading(true);
            setError("");


            await releaseInstallment(
                installmentId
            );


            setSuccessMessage(
                "Fund released successfully."
            );


            await loadData();

        } catch (err) {

            console.error(
                "Release installment error:",
                err
            );

            setError(
                err?.response?.data?.message ||
                err?.response?.data ||
                "Could not release this installment. Please try again."
            );

        } finally {

            setActionLoading(false);

        }
    };


    // =================================================
    // CLOSE SUCCESS MESSAGE
    // =================================================

    const handleCloseSuccess = () => {

        setSuccessMessage("");

    };


    // =================================================
    // UI
    // =================================================

    return (

        <MainLayout>

            {/* =========================================
                PAGE TITLE
            ========================================= */}

            <Typography
                variant="h4"
                sx={{
                    fontWeight: 600,
                    mb: 3
                }}
            >
                Staged Disbursement & Compliance Milestones
            </Typography>


            {/* =========================================
                ERROR MESSAGE
            ========================================= */}

            {error && (

                <Alert
                    severity="error"
                    sx={{ mb: 2 }}
                    onClose={() => setError("")}
                >
                    {String(error)}
                </Alert>

            )}


            {/* =========================================
                CREATE DISBURSEMENT PLAN
            ========================================= */}

            <Paper
                sx={{
                    p: 3,
                    mb: 3
                }}
            >

                <Typography
                    variant="h6"
                    sx={{
                        fontWeight: 600,
                        mb: 2
                    }}
                >
                    Create Disbursement Plan
                </Typography>


                <Divider
                    sx={{
                        mb: 3
                    }}
                />


                <Box
                    component="form"
                    onSubmit={handleCreatePlan}
                    sx={{
                        display: "flex",
                        flexWrap: "wrap",
                        gap: 2,
                        alignItems: "flex-end"
                    }}
                >

                    {/* APPLICATION */}

                    <TextField
                        select
                        label="Application"
                        value={applicationId}
                        onChange={(event) =>
                            setApplicationId(
                                event.target.value
                            )
                        }
                        sx={{
                            minWidth: 280
                        }}
                    >

                        {applications.length === 0 ? (

                            <MenuItem disabled>
                                No applications available
                            </MenuItem>

                        ) : (

                            applications.map(
                                (application) => (

                                    <MenuItem
                                        key={
                                            application.applicationId
                                        }
                                        value={
                                            application.applicationId
                                        }
                                    >
                                        Application #
                                        {
                                            application.applicationId
                                        }
                                    </MenuItem>

                                )
                            )

                        )}

                    </TextField>


                    {/* TOTAL AMOUNT */}

                    <TextField
                        label="Total Grant Amount"
                        type="number"
                        value={totalAmount}
                        onChange={(event) =>
                            setTotalAmount(
                                event.target.value
                            )
                        }
                        inputProps={{
                            min: 1
                        }}
                        sx={{
                            minWidth: 220
                        }}
                    />


                    {/* NUMBER OF INSTALLMENTS */}

                    <TextField
                        label="Number of Installments"
                        type="number"
                        value={numberOfInstallments}
                        onChange={(event) =>
                            setNumberOfInstallments(
                                event.target.value
                            )
                        }
                        inputProps={{
                            min: 1,
                            max: 10
                        }}
                        sx={{
                            minWidth: 220
                        }}
                    />


                    {/* CREATE BUTTON */}

                    <Button
                        type="submit"
                        variant="contained"
                        disabled={creatingPlan}
                        sx={{
                            height: 56,
                            px: 4
                        }}
                    >

                        {creatingPlan
                            ? "Creating..."
                            : "Create Disbursement Plan"
                        }

                    </Button>

                </Box>

            </Paper>


            {/* =========================================
                REMINDERS
            ========================================= */}

            {reminders.length > 0 && (

                <Alert
                    severity="warning"
                    sx={{
                        mb: 3
                    }}
                >

                    {reminders.length} milestone
                    {reminders.length > 1 ? "s" : ""}
                    {" "}
                    due within the next 3 days.

                </Alert>

            )}


            {/* =========================================
                PLAN SUMMARY
            ========================================= */}

            {!loading && plans.length > 0 && (

                <Paper
                    sx={{
                        p: 2,
                        mb: 3
                    }}
                >

                    <Typography
                        variant="h6"
                        sx={{
                            fontWeight: 600,
                            mb: 2
                        }}
                    >
                        Disbursement Plans
                    </Typography>


                    <Divider
                        sx={{
                            mb: 2
                        }}
                    />


                    <Box
                        sx={{
                            display: "flex",
                            flexWrap: "wrap",
                            gap: 2
                        }}
                    >

                        {plans.map((plan) => (

                            <Paper
                                key={plan.planId}
                                variant="outlined"
                                sx={{
                                    p: 2,
                                    minWidth: 220
                                }}
                            >

                                <Typography
                                    variant="subtitle1"
                                    sx={{
                                        fontWeight: 600
                                    }}
                                >
                                    Plan #{plan.planId}
                                </Typography>


                                <Typography variant="body2">
                                    Grant Amount: ₹
                                    {plan.totalGrantAmount}
                                </Typography>


                                <Typography variant="body2">
                                    Installments:{" "}
                                    {plan.numberOfInstallments}
                                </Typography>


                                <Typography variant="body2">
                                    Created:{" "}
                                    {plan.createdDate || "-"}
                                </Typography>


                                <Box
                                    sx={{
                                        mt: 1
                                    }}
                                >

                                    <Chip
                                        size="small"
                                        label={
                                            plan.status ||
                                            "Unknown"
                                        }
                                        color={statusColor(
                                            plan.status
                                        )}
                                    />

                                </Box>

                            </Paper>

                        ))}

                    </Box>

                </Paper>

            )}


            {/* =========================================
                INSTALLMENTS TABLE
            ========================================= */}

            <Paper>

                <Box
                    sx={{
                        p: 2
                    }}
                >

                    <Typography
                        variant="h6"
                        sx={{
                            fontWeight: 600
                        }}
                    >
                        Installments & Milestones
                    </Typography>

                </Box>


                <Divider />


                {/* LOADING */}

                {loading ? (

                    <Box
                        sx={{
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                            p: 5
                        }}
                    >

                        <CircularProgress />

                    </Box>

                ) : installments.length === 0 ? (

                    /* NO INSTALLMENTS */

                    <Alert
                        severity="info"
                        sx={{
                            m: 2
                        }}
                    >
                        No disbursement installments found.
                    </Alert>

                ) : (

                    /* INSTALLMENTS TABLE */

                    <TableContainer>

                        <Table>

                            {/* TABLE HEADER */}

                            <TableHead>

                                <TableRow>

                                    <TableCell>
                                        <b>Installment</b>
                                    </TableCell>

                                    <TableCell>
                                        <b>Plan ID</b>
                                    </TableCell>

                                    <TableCell>
                                        <b>Amount</b>
                                    </TableCell>

                                    <TableCell>
                                        <b>Scheduled Date</b>
                                    </TableCell>

                                    <TableCell>
                                        <b>Milestone</b>
                                    </TableCell>

                                    <TableCell>
                                        <b>Milestone Status</b>
                                    </TableCell>

                                    <TableCell>
                                        <b>Installment Status</b>
                                    </TableCell>

                                    <TableCell>
                                        <b>Action</b>
                                    </TableCell>

                                </TableRow>

                            </TableHead>


                            {/* TABLE BODY */}

                            <TableBody>

                                {installments.map(
                                    (inst) => {

                                        const milestone =
                                            inst.milestone;

                                        const milestoneStatus =
                                            milestone?.status?.toLowerCase() || "";

                                        const isCompleted =
                                            milestoneStatus === "completed";

                                        const isNonCompliant =
                                            milestoneStatus === "non-compliant";

                                        const isInProgress =
                                            milestoneStatus === "in progress";

                                        const isOverdue =
                                            milestoneStatus === "overdue";

                                        const milestoneDone = isCompleted;

                                        const alreadyReleased =
                                            inst.status &&
                                            inst.status
                                                .toLowerCase() ===
                                                "released";


                                        return (

                                            <TableRow
                                                key={
                                                    inst.installmentId
                                                }
                                                hover
                                            >

                                                {/* INSTALLMENT */}

                                                <TableCell>
                                                    #
                                                    {
                                                        inst.installmentNumber
                                                    }
                                                </TableCell>


                                                {/* PLAN */}

                                                <TableCell>
                                                    {
                                                        inst
                                                            .disbursementPlan
                                                            ?.planId ||
                                                        "-"
                                                    }
                                                </TableCell>


                                                {/* AMOUNT */}

                                                <TableCell>
                                                    ₹
                                                    {
                                                        inst.installmentAmount
                                                    }
                                                </TableCell>


                                                {/* DATE */}

                                                <TableCell>
                                                    {
                                                        inst.scheduledDate ||
                                                        "-"
                                                    }
                                                </TableCell>


                                                {/* MILESTONE */}

                                                <TableCell>
                                                    {
                                                        milestone
                                                            ?.milestoneType ||
                                                        "-"
                                                    }
                                                </TableCell>


                                                {/* MILESTONE STATUS */}

                                                <TableCell>

                                                    <Chip
                                                        size="small"
                                                        label={
                                                            milestone
                                                                ?.status ||
                                                            "-"
                                                        }
                                                        color={statusColor(
                                                            milestone
                                                                ?.status
                                                        )}
                                                    />

                                                </TableCell>


                                                {/* INSTALLMENT STATUS */}

                                                <TableCell>

                                                    <Chip
                                                        size="small"
                                                        label={
                                                            inst.status ||
                                                            "-"
                                                        }
                                                        color={statusColor(
                                                            inst.status
                                                        )}
                                                    />

                                                </TableCell>


                                                {/* ACTION */}

                                                <TableCell>

                                                    <Box
                                                        sx={{
                                                            display:
                                                                "flex",
                                                            gap: 1,
                                                            flexWrap:
                                                                "wrap"
                                                        }}
                                                    >

                                                        {/* MARK IN PROGRESS */}

                                                        {milestone && canMarkInProgress &&
                                                            !isCompleted && !isNonCompliant &&
                                                            !isInProgress && !isOverdue && (

                                                            <Button
                                                                variant="outlined"
                                                                size="small"
                                                                disabled={actionLoading}
                                                                onClick={() =>
                                                                    handleInProgress(
                                                                        milestone.milestoneId
                                                                    )
                                                                }
                                                            >
                                                                Mark In Progress
                                                            </Button>

                                                        )}


                                                        {/* COMPLETE MILESTONE */}

                                                        {!milestoneDone &&
                                                            milestone &&
                                                            canMarkComplete &&
                                                            !isNonCompliant &&
                                                            milestone.status
                                                                ?.toLowerCase() !==
                                                                "overdue" && (

                                                                <Button
                                                                    variant="outlined"
                                                                    size="small"
                                                                    disabled={
                                                                        actionLoading
                                                                    }
                                                                    onClick={() =>
                                                                        handleCompleteMilestone(
                                                                            milestone.milestoneId
                                                                        )
                                                                    }
                                                                >
                                                                    Mark Milestone Complete
                                                                </Button>

                                                            )}


                                                        {/* MARK NON-COMPLIANT */}

                                                        {milestone && canMarkNonCompliant &&
                                                            !isCompleted && !isNonCompliant && (

                                                            <Button
                                                                variant="outlined"
                                                                color="error"
                                                                size="small"
                                                                disabled={actionLoading}
                                                                onClick={() =>
                                                                    handleNonCompliant(
                                                                        milestone.milestoneId
                                                                    )
                                                                }
                                                            >
                                                                Mark Non-Compliant
                                                            </Button>

                                                        )}


                                                        {/* OVERDUE */}

                                                        {isOverdue && (

                                                            <Chip
                                                                size="small"
                                                                color="error"
                                                                label="Overdue"
                                                            />

                                                        )}


                                                        {/* RELEASE FUND */}

                                                        {!alreadyReleased && (

                                                            <Button
                                                                variant="contained"
                                                                color="success"
                                                                size="small"
                                                                disabled={
                                                                    !milestoneDone ||
                                                                    actionLoading
                                                                }
                                                                onClick={() =>
                                                                    handleRelease(
                                                                        inst.installmentId
                                                                    )
                                                                }
                                                            >
                                                                Release Fund
                                                            </Button>

                                                        )}


                                                        {/* ALREADY RELEASED */}

                                                        {alreadyReleased && (

                                                            <Chip
                                                                size="small"
                                                                color="success"
                                                                label="Fund Released"
                                                            />

                                                        )}

                                                    </Box>

                                                </TableCell>

                                            </TableRow>

                                        );

                                    }
                                )}

                            </TableBody>

                        </Table>

                    </TableContainer>

                )}

            </Paper>


            {/* =========================================
                SUCCESS MESSAGE
            ========================================= */}

            <Snackbar
                open={Boolean(successMessage)}
                autoHideDuration={3000}
                onClose={handleCloseSuccess}
                message={successMessage}
            />

        </MainLayout>

    );
}


export default Disbursement;