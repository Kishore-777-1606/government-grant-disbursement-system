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
    Chip
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";
import { getAllEligibilityRecords } from "../../services/eligibilityService";

function Eligibility() {

    const [records, setRecords] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadEligibilityRecords();
    }, []);

    const loadEligibilityRecords = async () => {

        try {

            const data = await getAllEligibilityRecords();
            setRecords(data);
            setError(null);

        } catch (err) {

            console.error(err);
            setError("Failed to load eligibility records.");

        } finally {

            setLoading(false);

        }

    };

    const getChipColor = (status) => {

        if (!status) return "default";

        const normalized = status.toLowerCase();

        if (normalized.includes("rejected") || normalized === "not eligible") {
            return "error";
        }

        if (normalized === "eligible" || normalized === "approved") {
            return "success";
        }

        return "warning";
    };

    return (

        <MainLayout>

            <Typography variant="h4" gutterBottom>
                Eligibility
            </Typography>

            {loading ? (

                <CircularProgress />

            ) : error ? (

                <Typography color="error">{error}</Typography>

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell><b>Application ID</b></TableCell>
                                <TableCell><b>Beneficiary</b></TableCell>
                                <TableCell><b>Scheme</b></TableCell>
                                <TableCell><b>Eligibility Score</b></TableCell>
                                <TableCell><b>Status</b></TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {records.length > 0 ? (

                                records.map((record) => (

                                    <TableRow key={record.applicationId} hover>

                                        <TableCell>{record.applicationId}</TableCell>

                                        <TableCell>{record.beneficiaryName}</TableCell>

                                        <TableCell>{record.schemeName}</TableCell>

                                        <TableCell>{record.eligibilityScore}</TableCell>

                                        <TableCell>
                                            <Chip
                                                label={record.status}
                                                color={getChipColor(record.status)}
                                            />
                                        </TableCell>

                                    </TableRow>

                                ))

                            ) : (

                                <TableRow>

                                    <TableCell colSpan={5} align="center">
                                        No eligibility records found.
                                    </TableCell>

                                </TableRow>

                            )}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

        </MainLayout>

    );
}

export default Eligibility;
