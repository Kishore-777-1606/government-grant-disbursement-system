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
// VIEW BENEFICIARY DOCUMENT (JWT-protected)
// =====================================================
//
// A plain <a href> can't attach the Authorization header, and the
// backend endpoint is intentionally protected — so we fetch the file
// through the shared `api` instance (which the JWT interceptor already
// covers), turn the response into a Blob, and open that in a new tab
// instead of linking straight to the backend URL.

export const viewBeneficiaryDocument = async (id) => {

    const response = await api.get(
        `/beneficiaries/${id}/document`,
        {
            responseType: "blob",
        }
    );

    const blobUrl = window.URL.createObjectURL(
        response.data
    );

    const newTab = window.open(blobUrl, "_blank");

    // If the popup was blocked, at least don't leave a dangling
    // object URL with nothing pointing at it.
    if (!newTab) {
        window.URL.revokeObjectURL(blobUrl);
        throw new Error(
            "Could not open the document — check if your browser blocked the popup."
        );
    }

    // Revoke once the new tab has had time to actually load the blob.
    // Revoking immediately can race the browser before it reads the file.
    setTimeout(() => {
        window.URL.revokeObjectURL(blobUrl);
    }, 30000);
};