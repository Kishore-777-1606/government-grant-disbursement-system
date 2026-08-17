import api from "./api";

export const getAllBeneficiaries = async () => {
    const response = await api.get("/beneficiaries");
    return response.data;
};

export const createBeneficiary = async (beneficiary) => {
    const response = await api.post("/beneficiaries", beneficiary);
    return response.data;
};

// Uploads (or replaces) a beneficiary's identity/eligibility proof document.
// Sent as multipart/form-data — must NOT use the default JSON Content-Type,
// so it's overridden here per-request.
export const uploadBeneficiaryDocument = async (id, file) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post(
        `/beneficiaries/${id}/document`,
        formData,
        { headers: { "Content-Type": "multipart/form-data" } }
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

// Direct link to view/download a beneficiary's uploaded document.
// Used as an <a href> target, not fetched via axios, so the browser
// handles the file (PDF preview / image / download) natively.
export const getBeneficiaryDocumentUrl = (id) => {
    return `http://localhost:8080/beneficiaries/${id}/document`;
};
export const updateBeneficiary = async (id, beneficiary) => {
    const response = await api.put(`/beneficiaries/${id}`, beneficiary);
    return response.data;
};

export const deleteBeneficiary = async (id) => {
    await api.delete(`/beneficiaries/${id}`);
};