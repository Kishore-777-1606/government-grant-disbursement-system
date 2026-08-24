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
    Chip,
    Alert
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";
import { getAllApplications } from "../../services/statusService";

function StatusTracking() {

    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
      const [error, setError] = useState("");

    useEffect(() => {
        loadApplications();
    }, []);

    const loadApplications = async () => {

        try {

            const data = await getAllApplications();
            setApplications(data);

               } catch (err) {

            console.error(err);
            setError(
                err?.response?.data?.message || "Unable to load application status data."
            );

        } finally {

            setLoading(false);

        }

    };

    const getChipColor = (status) => {

        if (!status) return "default";

        switch (status.toLowerCase()) {

            case "approved":
                return "success";

            case "rejected":
                return "error";

            case "verification pending":
            case "finance approval pending":
                return "warning";

            default:
                return "info";
        }

    };

    return (

        <MainLayout>

                       <Typography variant="h4" gutterBottom>
                Application Status Tracking
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

                        {loading ? (

                <CircularProgress />

            ) : applications.length === 0 ? (

                <Alert severity="info">No applications found.</Alert>

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell><b>Application ID</b></TableCell>
                                <TableCell><b>Beneficiary ID</b></TableCell>
                                <TableCell><b>Scheme ID</b></TableCell>
                                <TableCell><b>Eligibility Score</b></TableCell>
                                <TableCell><b>Status</b></TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {applications.map((app) => (

                                <TableRow key={app.applicationId} hover>

                                    <TableCell>{app.applicationId}</TableCell>

                                    <TableCell>{app.beneficiaryId}</TableCell>

                                    <TableCell>{app.schemeId}</TableCell>

                                    <TableCell>{app.eligibilityScore}</TableCell>

                                    <TableCell>

                                        <Chip
                                            label={app.status}
                                            color={getChipColor(app.status)}
                                        />

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

export default StatusTracking;