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
    Box,
    Button,
    IconButton,
    Snackbar,
    Alert
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

import MainLayout from "../../layouts/MainLayout";
import { getAllSchemes, deleteScheme } from "../../services/schemeService";
import AddEditSchemeDialog from "../../components/schemes/AddEditSchemeDialog";

function Schemes() {

    const [schemes, setSchemes] = useState([]);
    const [loading, setLoading] = useState(true);

    const [openDialog, setOpenDialog] = useState(false);
    const [schemeToEdit, setSchemeToEdit] = useState(null);

    const [snackbar, setSnackbar] = useState({
        open: false,
        message: "",
        severity: "success"
    });

    // Get current user's role from localStorage
    const stored = localStorage.getItem("user");
    const currentRole = stored ? JSON.parse(stored)?.role : null;

    const canCreateEdit = ["DISTRICT_OFFICER", "ADMIN"].includes(currentRole);
    const canDelete = currentRole === "ADMIN";

    useEffect(() => {
        loadSchemes();
    }, []);

    const loadSchemes = async () => {

        try {

            const data = await getAllSchemes();
            setSchemes(data);

        } catch (err) {

            console.error(err);

            setSnackbar({
                open: true,
                message: "Failed to load schemes",
                severity: "error"
            });

        } finally {

            setLoading(false);

        }

    };

    const handleAddClick = () => {
        setSchemeToEdit(null);
        setOpenDialog(true);
    };

    const handleEditClick = (scheme) => {
        setSchemeToEdit(scheme);
        setOpenDialog(true);
    };

    const handleDeleteClick = async (scheme) => {

        if (!window.confirm(`Delete scheme "${scheme.name}"?`)) {
            return;
        }

        try {

            await deleteScheme(scheme.id);

            setSnackbar({
                open: true,
                message: "Scheme deleted successfully",
                severity: "success"
            });

            loadSchemes();

        } catch (err) {

            console.error(err);

            const message =
                err?.response?.data?.message ||
                "Failed to delete scheme";

            setSnackbar({
                open: true,
                message,
                severity: "error"
            });

        }

    };

    return (

        <MainLayout>

            <Box
                display="flex"
                justifyContent="space-between"
                alignItems="center"
                mb={3}
            >

                <Typography variant="h4">
                    Government Schemes
                </Typography>

                {canCreateEdit && (
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={handleAddClick}
                    >
                        Add Scheme
                    </Button>
                )}

            </Box>

            {loading ? (

                <CircularProgress />

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell><b>ID</b></TableCell>
                                <TableCell><b>Scheme Code</b></TableCell>
                                <TableCell><b>Name</b></TableCell>
                                <TableCell><b>Amount</b></TableCell>
                                <TableCell><b>Start Date</b></TableCell>
                                <TableCell><b>End Date</b></TableCell>
                                <TableCell><b>Status</b></TableCell>
                                <TableCell><b>Actions</b></TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {schemes.length > 0 ? (

                                schemes.map((scheme) => (

                                    <TableRow
                                        key={scheme.id}
                                        hover
                                    >

                                        <TableCell>
                                            {scheme.id}
                                        </TableCell>

                                        <TableCell>
                                            {scheme.schemeCode}
                                        </TableCell>

                                        <TableCell>
                                            {scheme.name}
                                        </TableCell>

                                        <TableCell>
                                            ₹{scheme.amount}
                                        </TableCell>

                                        <TableCell>
                                            {scheme.startDate}
                                        </TableCell>

                                        <TableCell>
                                            {scheme.endDate}
                                        </TableCell>

                                        <TableCell>
                                            {scheme.isActive
                                                ? "Active"
                                                : "Inactive"}
                                        </TableCell>

                                        <TableCell>

                                            {canCreateEdit && (
                                                <IconButton
                                                    size="small"
                                                    onClick={() =>
                                                        handleEditClick(scheme)
                                                    }
                                                >
                                                    <EditIcon fontSize="small" />
                                                </IconButton>
                                            )}

                                            {canDelete && (
                                                <IconButton
                                                    size="small"
                                                    color="error"
                                                    onClick={() =>
                                                        handleDeleteClick(scheme)
                                                    }
                                                >
                                                    <DeleteIcon fontSize="small" />
                                                </IconButton>
                                            )}

                                        </TableCell>

                                    </TableRow>

                                ))

                            ) : (

                                <TableRow>

                                    <TableCell
                                        colSpan={8}
                                        align="center"
                                    >
                                        No schemes found.
                                    </TableCell>

                                </TableRow>

                            )}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

            <AddEditSchemeDialog
                open={openDialog}
                handleClose={() => setOpenDialog(false)}
                refreshData={loadSchemes}
                schemeToEdit={schemeToEdit}
            />

            <Snackbar
                open={snackbar.open}
                autoHideDuration={4000}
                onClose={() =>
                    setSnackbar((prev) => ({
                        ...prev,
                        open: false
                    }))
                }
                anchorOrigin={{
                    vertical: "bottom",
                    horizontal: "center"
                }}
            >
                <Alert
                    severity={snackbar.severity}
                    onClose={() =>
                        setSnackbar((prev) => ({
                            ...prev,
                            open: false
                        }))
                    }
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>

        </MainLayout>

    );

}

export default Schemes;