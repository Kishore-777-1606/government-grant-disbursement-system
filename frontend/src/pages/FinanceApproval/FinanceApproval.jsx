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
    Box
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";

import {
    getAllApprovals,
    approveFinance,
    rejectFinance
} from "../../services/financeService";

function FinanceApproval() {

    const [approvals, setApprovals] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadApprovals();
    }, []);

    const loadApprovals = async () => {

        try {

            const data = await getAllApprovals();
            setApprovals(data);

        } catch (err) {

            console.error(err);

        } finally {

            setLoading(false);

        }

    };

    const handleApprove = async (id) => {
    try {
        await approveFinance(id);
        loadApprovals();
    } catch (err) {
        console.error(err);
        alert(err?.response?.data?.message || "Failed to approve — please try again");
    }
};

const handleReject = async (id) => {
    try {
        await rejectFinance(id);
        loadApprovals();
    } catch (err) {
        console.error(err);
        alert(err?.response?.data?.message || "Failed to reject — please try again");
    }
};

    return (

        <MainLayout>

            <Typography variant="h4" gutterBottom>
                Finance Approval
            </Typography>

            {loading ? (

                <CircularProgress />

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell><b>ID</b></TableCell>
                                <TableCell><b>Application</b></TableCell>
                                <TableCell><b>Officer</b></TableCell>
                                <TableCell><b>Status</b></TableCell>
                                <TableCell><b>Remarks</b></TableCell>
                                <TableCell><b>Action</b></TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {approvals.map((a) => (

                                <TableRow key={a.approvalId} hover>

                                    <TableCell>{a.approvalId}</TableCell>

                                    <TableCell>
                                        {a.application.applicationId}
                                    </TableCell>

                                    <TableCell>
                                        {a.approvedBy}
                                    </TableCell>

                                    <TableCell>

                                        <Chip
                                            label={a.approvalStatus}
                                            color={
                                                a.approvalStatus === "Approved"
                                                    ? "success"
                                                    : a.approvalStatus === "Rejected"
                                                    ? "error"
                                                    : "warning"
                                            }
                                        />

                                    </TableCell>

                                    <TableCell>{a.remarks}</TableCell>

                                    <TableCell>

                                        <Box display="flex" gap={1}>

                                            <Button
                                                variant="contained"
                                                color="success"
                                                size="small"
                                                onClick={() => handleApprove(a.approvalId)}
                                            >
                                                Approve
                                            </Button>

                                            <Button
                                                variant="contained"
                                                color="error"
                                                size="small"
                                                onClick={() => handleReject(a.approvalId)}
                                            >
                                                Reject
                                            </Button>

                                        </Box>

                                    </TableCell>

                                </TableRow>

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

        </MainLayout>

    );

}

export default FinanceApproval;