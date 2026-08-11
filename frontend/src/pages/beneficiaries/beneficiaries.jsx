import { useEffect, useRef, useState } from "react";
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
    Alert
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import UploadFileIcon from "@mui/icons-material/UploadFile";

import MainLayout from "../../layouts/MainLayout";
import {
    getAllBeneficiaries,
    uploadBeneficiaryDocument,
    getBeneficiaryDocumentUrl
} from "../../services/beneficiaryService";
import AddBeneficiaryDialog from "../../components/beneficiaries/AddBeneficiaryDialog";

function Beneficiaries() {

    const [beneficiaries, setBeneficiaries] = useState([]);
    const [loading, setLoading] = useState(true);

    const [openDialog, setOpenDialog] = useState(false);

    const [uploadTargetId, setUploadTargetId] = useState(null);
    const fileInputRef = useRef(null);
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

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
            setSnackbar({ open: true, message: "Document uploaded successfully", severity: "success" });
            loadBeneficiaries();

        } catch (err) {

            console.error(err);

            const backendMessage =
                err?.response?.data?.message ||
                "Failed to upload document";

            setSnackbar({ open: true, message: backendMessage, severity: "error" });

        } finally {

            e.target.value = "";
            setUploadTargetId(null);

        }

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

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenDialog(true)}
                >
                    Add Beneficiary
                </Button>

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
                                <TableCell>Document</TableCell>

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

                                        {b.documentPath ? (

                                            <>
                                                <Link
                                                    href={getBeneficiaryDocumentUrl(b.id)}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    sx={{ mr: 1 }}
                                                >
                                                    {b.documentOriginalName || "View"}
                                                </Link>

                                                <Button
                                                    size="small"
                                                    onClick={() => handleUploadClick(b.id)}
                                                >
                                                    Replace
                                                </Button>
                                            </>

                                        ) : (

                                            <Button
                                                size="small"
                                                startIcon={<UploadFileIcon />}
                                                onClick={() => handleUploadClick(b.id)}
                                            >
                                                Upload
                                            </Button>

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
                handleClose={() => setOpenDialog(false)}
                refreshData={loadBeneficiaries}
            />

            <Snackbar
                open={snackbar.open}
                autoHideDuration={4000}
                onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
                anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
            >
                <Alert
                    severity={snackbar.severity}
                    onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>

        </MainLayout>

    );

}

export default Beneficiaries;