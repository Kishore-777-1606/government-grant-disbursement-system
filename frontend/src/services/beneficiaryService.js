import api from "./api";

// =====================================================
// GET ALL BENEFICIARIES
// =====================================================

export const getAllBeneficiaries = async () => {

    const response = await api.get(
        "/beneficiaries"
    );

    return response.data;
};

// =====================================================
// GET BENEFICIARY BY ID
// =====================================================

export const getBeneficiaryById = async (id) => {

    const response = await api.get(
        `/beneficiaries/${id}`
    );

    return response.data;
};

// =====================================================
// CREATE BENEFICIARY
// =====================================================

export const createBeneficiary = async (
    beneficiary
) => {

    const response = await api.post(
        "/beneficiaries",
        beneficiary
    );

    return response.data;
};

// =====================================================
// UPDATE BENEFICIARY
// =====================================================

export const updateBeneficiary = async (
    id,
    beneficiary
) => {

    const response = await api.put(
        `/beneficiaries/${id}`,
        beneficiary
    );

    return response.data;
};

// =====================================================
// DELETE BENEFICIARY
// =====================================================

export const deleteBeneficiary = async (id) => {

    const response = await api.delete(
        `/beneficiaries/${id}`
    );

    return response.data;
};

// =====================================================
// UPLOAD BENEFICIARY DOCUMENT
// =====================================================

export const uploadBeneficiaryDocument = async (
    id,
    file
) => {

    const formData = new FormData();

    formData.append("file", file);

    const response = await api.post(
        `/beneficiaries/${id}/document`,
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        }
    );

    return response.data;
};

export const updateBeneficiaryVerification = async (id, aadhaarVerified, bankVerified) => {
    const response = await api.put(`/beneficiaries/${id}/verification`, {
        aadhaarVerified,
        bankVerified
    });
    return response.data;
};

// =====================================================
// BENEFICIARY DOCUMENT URL
// =====================================================

export const getBeneficiaryDocumentUrl = (id) => {
    return `http://localhost:8080/beneficiaries/${id}/document`;
};