import NewApplicationDialog from "../../components/applications/NewApplicationDialog";
import { useEffect, useState } from "react";
import {
    Typography,
    Paper,
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell,
    TableContainer,
    CircularProgress,
    Button,
    Stack
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";
import { getAllApplications } from "../../services/applicationService";

function Applications() {

    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);

    useEffect(() => {
        loadApplications();
    }, []);

    const loadApplications = async () => {
    try {
    const data = await getAllApplications();

console.log("Applications:", data);



        setApplications(data);
    } catch (err) {
        console.error(err);
    } finally {
        setLoading(false);
    }
};

    return (

        <MainLayout>

            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Typography variant="h4">
                    Applications
                </Typography>

                <Button
    variant="contained"
    onClick={() => setOpenDialog(true)}
>
    New Application
</Button>

            </Stack>

            {loading ? (

                <CircularProgress />

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell>ID</TableCell>
                                <TableCell>Beneficiary ID</TableCell>
                                <TableCell>Scheme ID</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Eligibility Score</TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {applications.map((app) => (

                                <TableRow key={app.applicationId}>

                                    <TableCell>{app.applicationId}</TableCell>

                                    <TableCell>{app.beneficiaryId}</TableCell>

                                    <TableCell>{app.schemeId}</TableCell>

                                    <TableCell>{app.status}</TableCell>

                                    <TableCell>{app.eligibilityScore}</TableCell>

                                </TableRow>

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}
            <NewApplicationDialog
    open={openDialog}
    handleClose={() => setOpenDialog(false)}
    refreshData={loadApplications}
/>

        </MainLayout>

    );
}

export default Applications;