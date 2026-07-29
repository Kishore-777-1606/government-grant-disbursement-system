import { useState } from "react";

import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Grid,
    Snackbar,
    Alert
} from "@mui/material";

import { createApplication } from "../../services/applicationService";

const EMPTY_APPLICATION = {
    beneficiaryId: "",
    schemeId: "",
    applicationDate: new Date().toISOString().split("T")[0],
    status: "Submitted",
    eligibilityScore: 0,
    remarks: "New Application"
};

function NewApplicationDialog({ open, handleClose, refreshData }) {

    const [application, setApplication] = useState(EMPTY_APPLICATION);
    const [errors, setErrors] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    const handleChange = (e) => {

        setApplication({
            ...application,
            [e.target.name]: e.target.value
        });
        setErrors((prev) => ({ ...prev, [e.target.name]: undefined }));

    };

    const validate = () => {

        const newErrors = {};

        if (!application.beneficiaryId || Number(application.beneficiaryId) <= 0) {
            newErrors.beneficiaryId = "A valid Beneficiary ID is required";
        }

        if (!application.schemeId || Number(application.schemeId) <= 0) {
            newErrors.schemeId = "A valid Scheme ID is required";
        }

        if (!application.applicationDate) {
            newErrors.applicationDate = "Application date is required";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async () => {

        if (!validate()) {
            return;
        }

        setSubmitting(true);

        try {

            await createApplication({
                ...application,
                beneficiaryId: Number(application.beneficiaryId),
                schemeId: Number(application.schemeId)
            });

            setSnackbar({ open: true, message: "Application submitted successfully", severity: "success" });
            refreshData();
            setApplication(EMPTY_APPLICATION);
            setErrors({});
            handleClose();

        } catch (err) {

            console.error(err);

            const backendMessage =
                err?.response?.data?.message ||
                (err?.response?.data?.fieldErrors &&
                    Object.values(err.response.data.fieldErrors).join(", ")) ||
                "Failed to submit application";

            setSnackbar({ open: true, message: backendMessage, severity: "error" });

        } finally {
            setSubmitting(false);
        }

    };

    const handleCancel = () => {
        setApplication(EMPTY_APPLICATION);
        setErrors({});
        handleClose();
    };

    return (

        <>
            <Dialog
                open={open}
                onClose={handleCancel}
                fullWidth
                maxWidth="sm"
            >

                <DialogTitle>
                    New Application
                </DialogTitle>

                <DialogContent>

                    <Grid container spacing={2} sx={{ mt: 1 }}>

                        <Grid item xs={12}>

                            <TextField
                                fullWidth
                                required
                                type="number"
                                label="Beneficiary ID"
                                name="beneficiaryId"
                                value={application.beneficiaryId}
                                onChange={handleChange}
                                error={Boolean(errors.beneficiaryId)}
                                helperText={errors.beneficiaryId}
                            />

                        </Grid>

                        <Grid item xs={12}>

                            <TextField
                                fullWidth
                                required
                                type="number"
                                label="Scheme ID"
                                name="schemeId"
                                value={application.schemeId}
                                onChange={handleChange}
                                error={Boolean(errors.schemeId)}
                                helperText={errors.schemeId}
                            />

                        </Grid>

                        <Grid item xs={12}>

                            <TextField
                                fullWidth
                                required
                                type="date"
                                label="Application Date"
                                name="applicationDate"
                                value={application.applicationDate}
                                InputLabelProps={{ shrink: true }}
                                onChange={handleChange}
                                error={Boolean(errors.applicationDate)}
                                helperText={errors.applicationDate}
                            />

                        </Grid>

                    </Grid>

                </DialogContent>

                <DialogActions>

                    <Button onClick={handleCancel} disabled={submitting}>
                        Cancel
                    </Button>

                    <Button
                        variant="contained"
                        onClick={handleSubmit}
                        disabled={submitting}
                    >
                        {submitting ? "Submitting..." : "Submit"}
                    </Button>

                </DialogActions>

            </Dialog>

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
        </>

    );

}

export default NewApplicationDialog;
