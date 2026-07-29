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
    Button,
    CircularProgress
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";

import {
    getAllVerifications,
    approveVerification,
    rejectVerification
} from "../../services/verificationService";

function Verification() {

    const [verifications, setVerifications] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadVerifications();
    }, []);

    const loadVerifications = async () => {

        try {

            const data = await getAllVerifications();
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

                                <TableCell>ID</TableCell>
                                <TableCell>Application</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Officer</TableCell>
                                <TableCell>Action</TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {verifications.map((v) => (

                                <TableRow key={v.verificationId}>

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

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

        </MainLayout>

    );

}

export default Verification;