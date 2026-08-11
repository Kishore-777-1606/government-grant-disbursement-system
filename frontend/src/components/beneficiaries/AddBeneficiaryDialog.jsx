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
    Alert,
    MenuItem
} from "@mui/material";

import { createBeneficiary } from "../../services/beneficiaryService";

// Must match the backend's @Pattern(regexp = "^(General|SC|ST|OBC|EWS)$")
// on Beneficiary.category exactly, or submission fails with a 400.
const CATEGORY_OPTIONS =["General", "OBC", "SC", "ST", "EWS"];

const EMPTY_BENEFICIARY = {
    beneficiaryUid: "",
    firstName: "",
    lastName: "",
    dateOfBirth: "",
    gender: "",
    mobileNumber: "",
    email: "",
    addressLine1: "",
    addressLine2: "",
    villageId: "",
    blockId: "",
    districtId: "",
    stateId: "",
    pincode: "",
    bankAccountNumber: "",
    ifscCode: "",
    bankName: "",
    category: "",
    annualIncome: "",
    aadhaarVerified: false,
    bankVerified: false,
    isActive: true
};

const MOBILE_REGEX = /^[6-9]\d{9}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PINCODE_REGEX = /^[1-9]\d{5}$/;
const IFSC_REGEX = /^[A-Z]{4}0[A-Z0-9]{6}$/;

function AddBeneficiaryDialog({ open, handleClose, refreshData }) {

    const [beneficiary, setBeneficiary] = useState(EMPTY_BENEFICIARY);
    const [errors, setErrors] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setBeneficiary({
            ...beneficiary,
            [name]: value
        });
        setErrors((prev) => ({ ...prev, [name]: undefined }));
    };

    const validate = () => {

        const newErrors = {};

        if (!beneficiary.beneficiaryUid || !beneficiary.beneficiaryUid.trim()) {
            newErrors.beneficiaryUid = "Beneficiary UID is required";
        }

        if (!beneficiary.firstName || !beneficiary.firstName.trim()) {
            newErrors.firstName = "First name is required";
        }

        if (!beneficiary.lastName || !beneficiary.lastName.trim()) {
            newErrors.lastName = "Last name is required";
        }

        if (!beneficiary.dateOfBirth) {
            newErrors.dateOfBirth = "Date of birth is required";
        } else if (new Date(beneficiary.dateOfBirth) >= new Date()) {
            newErrors.dateOfBirth = "Date of birth must be in the past";
        }

        if (!beneficiary.gender || !beneficiary.gender.trim()) {
            newErrors.gender = "Gender is required";
        }

        if (!beneficiary.mobileNumber || !beneficiary.mobileNumber.trim()) {
            newErrors.mobileNumber = "Mobile number is required";
        } else if (!MOBILE_REGEX.test(beneficiary.mobileNumber.trim())) {
            newErrors.mobileNumber = "Enter a valid 10-digit mobile number";
        }

        if (!beneficiary.email || !beneficiary.email.trim()) {
            newErrors.email = "Email is required";
        } else if (!EMAIL_REGEX.test(beneficiary.email.trim())) {
            newErrors.email = "Enter a valid email address";
        }

        if (!beneficiary.pincode || !beneficiary.pincode.trim()) {
            newErrors.pincode = "Pincode is required";
        } else if (!PINCODE_REGEX.test(beneficiary.pincode.trim())) {
            newErrors.pincode = "Enter a valid 6-digit pincode";
        }

       if (beneficiary.ifscCode && beneficiary.ifscCode.trim() && !IFSC_REGEX.test(beneficiary.ifscCode.trim().toUpperCase())) {
            newErrors.ifscCode = "Enter a valid IFSC code (e.g. SBIN0001234)";
        }

        if (!beneficiary.category || !beneficiary.category.trim()) {
            newErrors.category = "Category is required";
        }

        if (beneficiary.annualIncome === "" || beneficiary.annualIncome === null || beneficiary.annualIncome === undefined) {
            newErrors.annualIncome = "Annual income is required";
        } else if (Number(beneficiary.annualIncome) < 0) {
            newErrors.annualIncome = "Annual income cannot be negative";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };
    // Optional numeric fields default to "" in form state (a controlled
    // MUI TextField can't hold null). Sent as-is, an empty string fails
    // Jackson's BigDecimal/Long binding on the backend with a 400 — so
    // blank must become null right before the request goes out.
    const toNullableNumber = (value) =>
        value === "" || value === null || value === undefined
            ? null
            : Number(value);

    const handleSubmit = async () => {

        if (!validate()) {
            return;
        }

        setSubmitting(true);

        try {

            await createBeneficiary({
                ...beneficiary,
                ifscCode: beneficiary.ifscCode ? beneficiary.ifscCode.toUpperCase() : beneficiary.ifscCode,
                villageId: toNullableNumber(beneficiary.villageId),
                blockId: toNullableNumber(beneficiary.blockId),
                districtId: toNullableNumber(beneficiary.districtId),
                stateId: toNullableNumber(beneficiary.stateId),
                annualIncome: toNullableNumber(beneficiary.annualIncome)
            });

            setSnackbar({ open: true, message: "Beneficiary added successfully", severity: "success" });
            refreshData();
            setBeneficiary(EMPTY_BENEFICIARY);
            setErrors({});
            handleClose();

        } catch (error) {

            console.error(error);

            const backendMessage =
                error?.response?.data?.message ||
                (error?.response?.data?.fieldErrors &&
                    Object.values(error.response.data.fieldErrors).join(", ")) ||
                "Failed to create beneficiary";

            setSnackbar({ open: true, message: backendMessage, severity: "error" });

        } finally {
            setSubmitting(false);
        }

    };

    const handleCancel = () => {
        setBeneficiary(EMPTY_BENEFICIARY);
        setErrors({});
        handleClose();
    };

    return (

        <>
            <Dialog
                open={open}
                onClose={handleCancel}
                maxWidth="md"
                fullWidth
            >

                <DialogTitle>
                    Add Beneficiary
                </DialogTitle>

                <DialogContent>

                    <Grid container spacing={2} sx={{ mt: 1 }}>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                label="Beneficiary UID"
                                name="beneficiaryUid"
                                value={beneficiary.beneficiaryUid}
                                onChange={handleChange}
                                error={Boolean(errors.beneficiaryUid)}
                                helperText={errors.beneficiaryUid}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                label="First Name"
                                name="firstName"
                                value={beneficiary.firstName}
                                onChange={handleChange}
                                error={Boolean(errors.firstName)}
                                helperText={errors.firstName}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                label="Last Name"
                                name="lastName"
                                value={beneficiary.lastName}
                                onChange={handleChange}
                                error={Boolean(errors.lastName)}
                                helperText={errors.lastName}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                type="date"
                                name="dateOfBirth"
                                label="Date of Birth"
                                value={beneficiary.dateOfBirth}
                                InputLabelProps={{ shrink: true }}
                                onChange={handleChange}
                                error={Boolean(errors.dateOfBirth)}
                                helperText={errors.dateOfBirth}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                label="Gender"
                                name="gender"
                                value={beneficiary.gender}
                                onChange={handleChange}
                                error={Boolean(errors.gender)}
                                helperText={errors.gender}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                label="Mobile Number"
                                name="mobileNumber"
                                value={beneficiary.mobileNumber}
                                onChange={handleChange}
                                error={Boolean(errors.mobileNumber)}
                                helperText={errors.mobileNumber}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                label="Email"
                                name="email"
                                value={beneficiary.email}
                                onChange={handleChange}
                                error={Boolean(errors.email)}
                                helperText={errors.email}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                label="Address Line 1"
                                name="addressLine1"
                                value={beneficiary.addressLine1}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                label="Address Line 2"
                                name="addressLine2"
                                value={beneficiary.addressLine2}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={3}>
                            <TextField
                                fullWidth
                                label="Village ID"
                                name="villageId"
                                value={beneficiary.villageId}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={3}>
                            <TextField
                                fullWidth
                                label="Block ID"
                                name="blockId"
                                value={beneficiary.blockId}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={3}>
                            <TextField
                                fullWidth
                                label="District ID"
                                name="districtId"
                                value={beneficiary.districtId}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={3}>
                            <TextField
                                fullWidth
                                label="State ID"
                                name="stateId"
                                value={beneficiary.stateId}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={4}>
                            <TextField
                                fullWidth
                                required
                                label="Pincode"
                                name="pincode"
                                value={beneficiary.pincode}
                                onChange={handleChange}
                                error={Boolean(errors.pincode)}
                                helperText={errors.pincode}
                            />
                        </Grid>

                        <Grid item xs={4}>
                            <TextField
                                fullWidth
                                label="Bank Name"
                                name="bankName"
                                value={beneficiary.bankName}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={4}>
                            <TextField
                                fullWidth
                                label="Account Number"
                                name="bankAccountNumber"
                                value={beneficiary.bankAccountNumber}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                label="IFSC Code"
                                name="ifscCode"
                                value={beneficiary.ifscCode}
                                onChange={handleChange}
                                error={Boolean(errors.ifscCode)}
                                helperText={errors.ifscCode}
                            />
                        </Grid>

                       <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                select
                                label="Category"
                                name="category"
                                value={beneficiary.category}
                                onChange={handleChange}
                                error={Boolean(errors.category)}
                                helperText={errors.category || "Used to check scheme eligibility criteria"}
                            >
                                {CATEGORY_OPTIONS.map((option) => (
                                    <MenuItem key={option} value={option}>
                                        {option}
                                    </MenuItem>
                                ))}
                            </TextField>
                        </Grid>

                        <Grid item xs={6}>
                            <TextField
                                fullWidth
                                required
                                type="number"
                                label="Annual Income"
                                name="annualIncome"
                                value={beneficiary.annualIncome}
                                onChange={handleChange}
                                error={Boolean(errors.annualIncome)}
                                helperText={errors.annualIncome || "Used to check scheme income-ceiling criteria"}
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

export default AddBeneficiaryDialog;