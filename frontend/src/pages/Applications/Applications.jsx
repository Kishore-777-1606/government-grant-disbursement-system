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
    Stack,
    IconButton
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

import MainLayout from "../../layouts/MainLayout";

import {
    getAllApplications,
    deleteApplication
} from "../../services/applicationService";

function Applications() {

    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);

    // Get currently logged-in user's role
    const stored = localStorage.getItem("user");
    const currentRole = stored
        ? JSON.parse(stored)?.role
        : null;

    // Application currently being edited
    const [applicationToEdit, setApplicationToEdit] =
        useState(null);

    // Role-based permissions
    const canEdit = [
        "FIELD_OFFICER",
        "DISTRICT_OFFICER",
        "ADMIN"
    ].includes(currentRole);

    const canDelete = currentRole === "ADMIN";

    useEffect(() => {
        loadApplications();
    }, []);

    const loadApplications = async () => {
        try {
            const data = await getAllApplications();

            setApplications(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Open application in edit mode
    const handleEditClick = (app) => {
        setApplicationToEdit(app);
        setOpenDialog(true);
    };

    // Delete application
    const handleDeleteClick = async (app) => {

        if (
            !window.confirm(
                `Delete application #${app.applicationId}?`
            )
        ) {
            return;
        }

        try {

            await deleteApplication(
                app.applicationId
            );

            await loadApplications();

        } catch (err) {

            console.error(err);

            alert(
                err?.response?.data?.message ||
                "Failed to delete application"
            );
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
                    onClick={() => {
                        // Clear edit state before creating
                        // a new application.
                        setApplicationToEdit(null);
                        setOpenDialog(true);
                    }}
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

                                <TableCell>
                                    ID
                                </TableCell>

                                <TableCell>
                                    Beneficiary ID
                                </TableCell>

                                <TableCell>
                                    Scheme ID
                                </TableCell>

                                <TableCell>
                                    Status
                                </TableCell>

                                <TableCell>
                                    Eligibility Score
                                </TableCell>

                                <TableCell>
                                    Actions
                                </TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {applications.map((app) => (

                                <TableRow
                                    key={app.applicationId}
                                >

                                    <TableCell>
                                        {app.applicationId}
                                    </TableCell>

                                    <TableCell>
                                        {app.beneficiaryId}
                                    </TableCell>

                                    <TableCell>
                                        {app.schemeId}
                                    </TableCell>

                                    <TableCell>
                                        {app.status}
                                    </TableCell>

                                    <TableCell>
                                        {app.eligibilityScore}
                                    </TableCell>

                                    <TableCell>

                                        {canEdit && (
                                            <IconButton
                                                size="small"
                                                onClick={() =>
                                                    handleEditClick(app)
                                                }
                                            >
                                                <EditIcon
                                                    fontSize="small"
                                                />
                                            </IconButton>
                                        )}

                                        {canDelete && (
                                            <IconButton
                                                size="small"
                                                onClick={() =>
                                                    handleDeleteClick(app)
                                                }
                                            >
                                                <DeleteIcon
                                                    fontSize="small"
                                                />
                                            </IconButton>
                                        )}

                                    </TableCell>

                                </TableRow>

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

            <NewApplicationDialog
                open={openDialog}
                handleClose={() =>
                    setOpenDialog(false)
                }
                refreshData={loadApplications}
                applicationToEdit={applicationToEdit}
            />

        </MainLayout>

    );
}

export default Applications;