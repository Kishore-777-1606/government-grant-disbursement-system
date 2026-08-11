import { Fragment, useEffect, useState } from "react";

import {
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Button,
    CircularProgress,
    Collapse,
    Box,
    IconButton,
    Chip
} from "@mui/material";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";

import MainLayout from "../../layouts/MainLayout";

import {
    getPendingVerifications,
    getVerificationHistory,
    approveVerification,
    rejectVerification
} from "../../services/verificationService";

function Verification() {

    // Only the currently-actionable stage per application. Historical
    // (Approved/Rejected/Sent Back) stages are preserved server-side for
    // audit purposes but aren't shown here as things to act on — expand a
    // row to see that application's full history instead.
    const [verifications, setVerifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [expandedId, setExpandedId] = useState(null);
    const [history, setHistory] = useState([]);
    const [historyLoading, setHistoryLoading] = useState(false);

    useEffect(() => {
        loadVerifications();
    }, []);

    const loadVerifications = async () => {

        try {

            const data = await getPendingVerifications();
            setVerifications(data);

        } catch (error) {

            console.error(error);

        } finally {

            setLoading(false);

        }

    };

    // The verification's "verifiedBy" field tells us which officer stage it
    // is currently waiting on, so the correct role is sent to the backend
    // instead of always assuming Field Officer.
    const roleForVerification = (verification) => {
        if (verification.verifiedBy && verification.verifiedBy.toLowerCase().includes("district")) {
            return "DISTRICT_OFFICER";
        }
        return "FIELD_OFFICER";
    };

    const approve = async (verification) => {

        try {
            await approveVerification(verification.verificationId, roleForVerification(verification));
            loadVerifications();
        } catch (err) {
            console.error(err);
            alert(err?.response?.data?.message || "Failed to approve verification");
        }

    };

    const reject = async (verification) => {

        try {
            await rejectVerification(verification.verificationId, roleForVerification(verification));
            loadVerifications();
        } catch (err) {
            console.error(err);
            alert(err?.response?.data?.message || "Failed to reject verification");
        }

    };

    const toggleHistory = async (verification) => {

        const applicationId = verification.application.applicationId;

        if (expandedId === verification.verificationId) {
            setExpandedId(null);
            return;
        }

        setExpandedId(verification.verificationId);
        setHistoryLoading(true);

        try {
            const data = await getVerificationHistory(applicationId);
            setHistory(data);
        } catch (err) {
            console.error(err);
            setHistory([]);
        } finally {
            setHistoryLoading(false);
        }

    };

    return (

        <MainLayout>

            <Typography variant="h4" gutterBottom>
                Verification
            </Typography>

            {loading ? (

                <CircularProgress />

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell />
                                <TableCell>ID</TableCell>
                                <TableCell>Application</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Officer</TableCell>
                                <TableCell>Action</TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {verifications.map((v) => (

                                <Fragment key={v.verificationId}>

                                <TableRow key={v.verificationId}>

                                    <TableCell>
                                        <IconButton
                                            size="small"
                                            onClick={() => toggleHistory(v)}
                                        >
                                            {expandedId === v.verificationId
                                                ? <KeyboardArrowUpIcon />
                                                : <KeyboardArrowDownIcon />}
                                        </IconButton>
                                    </TableCell>

                                    <TableCell>
                                        {v.verificationId}
                                    </TableCell>

                                    <TableCell>
                                        {v.application.applicationId}
                                    </TableCell>

                                    <TableCell>
                                        {v.verificationStatus}
                                    </TableCell>

                                    <TableCell>
                                        {v.verifiedBy}
                                    </TableCell>

                                    <TableCell>

                                        <Button
                                            variant="contained"
                                            color="success"
                                            size="small"
                                            sx={{ mr: 1 }}
                                            onClick={() => approve(v)}
                                        >
                                            Approve
                                        </Button>

                                        <Button
                                            variant="contained"
                                            color="error"
                                            size="small"
                                            onClick={() => reject(v)}
                                        >
                                            Reject
                                        </Button>

                                    </TableCell>

                                </TableRow>

                                <TableRow key={`${v.verificationId}-history`}>
                                    <TableCell
                                        style={{ paddingBottom: 0, paddingTop: 0 }}
                                        colSpan={6}
                                    >
                                        <Collapse
                                            in={expandedId === v.verificationId}
                                            timeout="auto"
                                            unmountOnExit
                                        >
                                            <Box sx={{ m: 2 }}>
                                                <Typography variant="subtitle2" gutterBottom>
                                                    Verification History — Application #{v.application.applicationId}
                                                </Typography>

                                                {historyLoading ? (
                                                    <CircularProgress size={20} />
                                                ) : (
                                                    <Table size="small">
                                                        <TableHead>
                                                            <TableRow>
                                                                <TableCell>Stage</TableCell>
                                                                <TableCell>Date</TableCell>
                                                                <TableCell>Status</TableCell>
                                                                <TableCell>Remarks</TableCell>
                                                            </TableRow>
                                                        </TableHead>
                                                        <TableBody>
                                                            {history.map((h) => (
                                                                <TableRow key={h.verificationId}>
                                                                    <TableCell>{h.verifiedBy}</TableCell>
                                                                    <TableCell>{h.verificationDate}</TableCell>
                                                                    <TableCell>
                                                                        <Chip
                                                                            label={h.verificationStatus}
                                                                            size="small"
                                                                        />
                                                                    </TableCell>
                                                                    <TableCell>{h.remarks}</TableCell>
                                                                </TableRow>
                                                            ))}
                                                        </TableBody>
                                                    </Table>
                                                )}
                                            </Box>
                                        </Collapse>
                                    </TableCell>
                                </TableRow>

                                </Fragment>

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

        </MainLayout>

    );

}

export default Verification;