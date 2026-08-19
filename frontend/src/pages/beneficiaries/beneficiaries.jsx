import { useEffect, useRef, useState } from "react";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { IconButton } from "@mui/material";
import {
    getAllBeneficiaries,
    uploadBeneficiaryDocument,
    getBeneficiaryDocumentUrl,
    deleteBeneficiary,
    updateBeneficiaryVerification
} from "../../services/beneficiaryService";
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
    Link,
    Snackbar,
    Alert,
    Checkbox,
    FormControlLabel
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import UploadFileIcon from "@mui/icons-material/UploadFile";

import MainLayout from "../../layouts/MainLayout";
import AddBeneficiaryDialog from "../../components/beneficiaries/AddBeneficiaryDialog";

function Beneficiaries() {

    // Get current user's role — MUST come before anything that uses currentRole
    const stored = localStorage.getItem("user");
    const currentRole = stored ? JSON.parse(stored)?.role : null;

    const canEdit = ["FIELD_OFFICER", "ADMIN"].includes(currentRole);
    const canDelete = currentRole === "ADMIN";
    const canCreate = ["FIELD_OFFICER", "ADMIN"].includes(currentRole);

    const [beneficiaryToEdit, setBeneficiaryToEdit] = useState(null);
    const [beneficiaries, setBeneficiaries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);
    const [uploadTargetId, setUploadTargetId] = useState(null);
    const fileInputRef = useRef(null);

    const [snackbar, setSnackbar] = useState({
        open: false,
        message: "",
        severity: "success"
    });

    useEffect(() => {
        loadBeneficiaries();
    }, []);

    const loadBeneficiaries = async () => {

        try {

            const data = await getAllBeneficiaries();
            setBeneficiaries(data);

        } catch (err) {

            console.error(err);

        } finally {

            setLoading(false);

        }

    };

    const handleUploadClick = (id) => {
        setUploadTargetId(id);
        fileInputRef.current?.click();
    };

    const handleFileSelected = async (e) => {

        const file = e.target.files[0];

        if (!file || uploadTargetId === null) {
            return;
        }

        try {

            await uploadBeneficiaryDocument(uploadTargetId, file);

            setSnackbar({
                open: true,
                message: "Document uploaded successfully",
                severity: "success"
            });

            loadBeneficiaries();

        } catch (err) {

            console.error(err);

            const backendMessage =
                err?.response?.data?.message ||
                "Failed to upload document";

            setSnackbar({
                open: true,
                message: backendMessage,
                severity: "error"
            });

        } finally {

            e.target.value = "";
            setUploadTargetId(null);

        }

    };

    const handleVerificationToggle = async (
        id,
        field,
        newAadhaar,
        newBank
    ) => {

        try {

            await updateBeneficiaryVerification(
                id,
                newAadhaar,
                newBank
            );

            setSnackbar({
                open: true,
                message: "Verification status updated",
                severity: "success"
            });

            loadBeneficiaries();

        } catch (err) {

            console.error(err);

            setSnackbar({
                open: true,
                message: "Failed to update verification",
                severity: "error"
            });

        }

    };

    const handleEditClick = (beneficiary) => {
        setBeneficiaryToEdit(beneficiary);
        setOpenDialog(true);
    };

    const handleDelete = async (id) => {

        if (!window.confirm("Delete this beneficiary? This cannot be undone.")) {
            return;
        }

        try {

            await deleteBeneficiary(id);

            setSnackbar({
                open: true,
                message: "Beneficiary deleted successfully",
                severity: "success"
            });

            loadBeneficiaries();

        } catch (err) {

            console.error(err);

            const backendMessage =
                err?.response?.data?.message ||
                "Failed to delete beneficiary";

            setSnackbar({
                open: true,
                message: backendMessage,
                severity: "error"
            });

        }

    };

    const handleDialogClose = () => {
        setOpenDialog(false);
        setBeneficiaryToEdit(null);
    };

    return (

        <MainLayout>

            <input
                type="file"
                ref={fileInputRef}
                style={{ display: "none" }}
                accept=".pdf,.jpg,.jpeg,.png"
                onChange={handleFileSelected}
            />

            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    marginBottom: 20
                }}
            >

                <Typography variant="h4">
                    Beneficiaries
                </Typography>

                {canCreate && (
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => setOpenDialog(true)}
                    >
                        Add Beneficiary
                    </Button>
                )}

            </div>

            {loading ? (

                <CircularProgress />

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell>ID</TableCell>
                                <TableCell>Name</TableCell>
                                <TableCell>Mobile</TableCell>
                                <TableCell>Email</TableCell>
                                <TableCell>Verification</TableCell>
                                <TableCell>Document</TableCell>
                                <TableCell>Actions</TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {beneficiaries.map((b) => (

                                <TableRow key={b.id}>

                                    <TableCell>{b.id}</TableCell>

                                    <TableCell>
                                        {b.firstName} {b.lastName}
                                    </TableCell>

                                    <TableCell>{b.mobileNumber}</TableCell>

                                    <TableCell>{b.email}</TableCell>

                                    <TableCell>

                                        <FormControlLabel
                                            control={
                                                <Checkbox
                                                    size="small"
                                                    checked={Boolean(
                                                        b.aadhaarVerified
                                                    )}
                                                    onChange={() =>
                                                        handleVerificationToggle(
                                                            b.id,
                                                            "aadhaarVerified",
                                                            !b.aadhaarVerified,
                                                            b.bankVerified
                                                        )
                                                    }
                                                />
                                            }
                                            label="Aadhaar"
                                        />

                                        <FormControlLabel
                                            control={
                                                <Checkbox
                                                    size="small"
                                                    checked={Boolean(
                                                        b.bankVerified
                                                    )}
                                                    onChange={() =>
                                                        handleVerificationToggle(
                                                            b.id,
                                                            "bankVerified",
                                                            b.aadhaarVerified,
                                                            !b.bankVerified
                                                        )
                                                    }
                                                />
                                            }
                                            label="Bank"
                                        />

                                    </TableCell>

                                    <TableCell>

                                        {b.documentPath ? (

                                            <>
                                                <Link
                                                    href={getBeneficiaryDocumentUrl(
                                                        b.id
                                                    )}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    sx={{ mr: 1 }}
                                                >
                                                    {b.documentOriginalName ||
                                                        "View"}
                                                </Link>

                                                <Button
                                                    size="small"
                                                    onClick={() =>
                                                        handleUploadClick(b.id)
                                                    }
                                                >
                                                    Replace
                                                </Button>
                                            </>

                                        ) : (

                                            <Button
                                                size="small"
                                                startIcon={
                                                    <UploadFileIcon />
                                                }
                                                onClick={() =>
                                                    handleUploadClick(b.id)
                                                }
                                            >
                                                Upload
                                            </Button>

                                        )}

                                    </TableCell>

                                    <TableCell>
                                        {canEdit && (
                                            <IconButton
                                                size="small"
                                                onClick={() => handleEditClick(b)}
                                            >
                                                <EditIcon fontSize="small" />
                                            </IconButton>
                                        )}
                                        {canDelete && (
                                            <IconButton
                                                size="small"
                                                onClick={() => handleDelete(b.id)}
                                            >
                                                <DeleteIcon fontSize="small" />
                                            </IconButton>
                                        )}
                                    </TableCell>

                                </TableRow>

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

            <AddBeneficiaryDialog
                open={openDialog}
                handleClose={handleDialogClose}
                refreshData={loadBeneficiaries}
                beneficiaryToEdit={beneficiaryToEdit}
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

export default Beneficiaries;