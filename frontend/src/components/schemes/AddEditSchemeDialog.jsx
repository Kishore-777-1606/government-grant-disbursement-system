import { useEffect, useState } from "react";

import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Grid,
    MenuItem,
    Snackbar,
    Alert
} from "@mui/material";

import { createScheme, updateScheme } from "../../services/schemeService";

const EMPTY_SCHEME = {
    schemeCode: "",
    name: "",
    description: "",
    schemeType: "",
    disbursementMode: "",
    frequency: "",
    amount: "",
    maxBeneficiaries: "",
    startDate: "",
    endDate: "",
    isActive: true
};

function AddEditSchemeDialog({ open, handleClose, refreshData, schemeToEdit }) {

    const isEditMode = Boolean(schemeToEdit);

    const [scheme, setScheme] = useState(EMPTY_SCHEME);
    const [errors, setErrors] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        if (open) {
            setScheme(schemeToEdit ? { ...EMPTY_SCHEME, ...schemeToEdit } : EMPTY_SCHEME);
            setErrors({});
        }
    }, [open, schemeToEdit]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setScheme((prev) => ({ ...prev, [name]: value }));
        setErrors((prev) => ({ ...prev, [name]: undefined }));
    };

    const validate = () => {
        const newErrors = {};

        if (!scheme.schemeCode || !scheme.schemeCode.trim()) {
            newErrors.schemeCode = "Scheme code is required";
        }

        if (!scheme.name || !scheme.name.trim()) {
            newErrors.name = "Scheme name is required";
        }

        if (scheme.amount === "" || scheme.amount === null || Number(scheme.amount) <= 0) {
            newErrors.amount = "Amount must be greater than zero";
        }

        if (!scheme.startDate) {
            newErrors.startDate = "Start date is required";
        }

        if (scheme.endDate && scheme.startDate && scheme.endDate < scheme.startDate) {
            newErrors.endDate = "End date cannot be before start date";
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

            const payload = {
                ...scheme,
                amount: Number(scheme.amount),
                maxBeneficiaries: scheme.maxBeneficiaries === "" ? null : Number(scheme.maxBeneficiaries)
            };

            if (isEditMode) {
                await updateScheme(schemeToEdit.id, payload);
                setSnackbar({ open: true, message: "Scheme updated successfully", severity: "success" });
            } else {
                await createScheme(payload);
                setSnackbar({ open: true, message: "Scheme created successfully", severity: "success" });
            }

            refreshData();
            handleClose();

        } catch (error) {

            console.error(error);

            const backendMessage =
                error?.response?.data?.message ||
                (error?.response?.data?.fieldErrors &&
                    Object.values(error.response.data.fieldErrors).join(", ")) ||
                "Failed to save scheme";

            setSnackbar({ open: true, message: backendMessage, severity: "error" });

        } finally {
            setSubmitting(false);
        }

    };

    return (
        <>
            <Dialog
                open={open}
                onClose={handleClose}
                maxWidth="md"
                fullWidth
            >

                <DialogTitle>
                    {isEditMode ? "Edit Scheme" : "Add Scheme"}
                </DialogTitle>

                <DialogContent>

                    <Grid container spacing={2} sx={{ mt: 1 }}>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                label="Scheme Code"
                                name="schemeCode"
                                value={scheme.schemeCode}
                                onChange={handleChange}
                                error={Boolean(errors.schemeCode)}
                                helperText={errors.schemeCode}
                                required
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                label="Scheme Name"
                                name="name"
                                value={scheme.name}
                                onChange={handleChange}
                                error={Boolean(errors.name)}
                                helperText={errors.name}
                                required
                            />
                        </Grid>

                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Description"
                                name="description"
                                value={scheme.description || ""}
                                onChange={handleChange}
                                multiline
                                minRows={2}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                select
                                label="Scheme Type"
                                name="schemeType"
                                value={scheme.schemeType || ""}
                                onChange={handleChange}
                            >
                                <MenuItem value="">Select</MenuItem>
                                <MenuItem value="Cash Transfer">Cash Transfer</MenuItem>
                                <MenuItem value="Subsidy">Subsidy</MenuItem>
                                <MenuItem value="Pension">Pension</MenuItem>
                                <MenuItem value="Scholarship">Scholarship</MenuItem>
                            </TextField>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                select
                                label="Disbursement Mode"
                                name="disbursementMode"
                                value={scheme.disbursementMode || ""}
                                onChange={handleChange}
                            >
                                <MenuItem value="">Select</MenuItem>
                                <MenuItem value="Bank Transfer">Bank Transfer</MenuItem>
                                <MenuItem value="Cheque">Cheque</MenuItem>
                                <MenuItem value="Cash">Cash</MenuItem>
                            </TextField>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                select
                                label="Frequency"
                                name="frequency"
                                value={scheme.frequency || ""}
                                onChange={handleChange}
                            >
                                <MenuItem value="">Select</MenuItem>
                                <MenuItem value="One-Time">One-Time</MenuItem>
                                <MenuItem value="Monthly">Monthly</MenuItem>
                                <MenuItem value="Quarterly">Quarterly</MenuItem>
                                <MenuItem value="Annually">Annually</MenuItem>
                            </TextField>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                type="number"
                                label="Amount"
                                name="amount"
                                value={scheme.amount}
                                onChange={handleChange}
                                error={Boolean(errors.amount)}
                                helperText={errors.amount}
                                required
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                type="number"
                                label="Max Beneficiaries"
                                name="maxBeneficiaries"
                                value={scheme.maxBeneficiaries || ""}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                type="date"
                                label="Start Date"
                                name="startDate"
                                value={scheme.startDate || ""}
                                InputLabelProps={{ shrink: true }}
                                onChange={handleChange}
                                error={Boolean(errors.startDate)}
                                helperText={errors.startDate}
                                required
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                type="date"
                                label="End Date"
                                name="endDate"
                                value={scheme.endDate || ""}
                                InputLabelProps={{ shrink: true }}
                                onChange={handleChange}
                                error={Boolean(errors.endDate)}
                                helperText={errors.endDate}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                select
                                label="Status"
                                name="isActive"
                                value={scheme.isActive ? "true" : "false"}
                                onChange={(e) =>
                                    setScheme((prev) => ({ ...prev, isActive: e.target.value === "true" }))
                                }
                            >
                                <MenuItem value="true">Active</MenuItem>
                                <MenuItem value="false">Inactive</MenuItem>
                            </TextField>
                        </Grid>

                    </Grid>

                </DialogContent>

                <DialogActions>

                    <Button onClick={handleClose} disabled={submitting}>
                        Cancel
                    </Button>

                    <Button
                        variant="contained"
                        onClick={handleSubmit}
                        disabled={submitting}
                    >
                        {submitting ? "Saving..." : "Save"}
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

export default AddEditSchemeDialog;
