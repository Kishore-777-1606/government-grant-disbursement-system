import api from "./api";

export const getAllVerifications = async () => {
    const response = await api.get("/verifications");
    return response.data;
};

// Only the currently-actionable stage per application (status "Pending").
// Historical stages (Approved / Rejected / Sent Back) are preserved for
// audit purposes but shouldn't show up as things to act on.
export const getPendingVerifications = async () => {
    const response = await api.get("/verifications/pending");
    return response.data;
};

export const getVerificationHistory = async (applicationId) => {
    const response = await api.get(
        `/verifications/application/${applicationId}/history`
    );
    return response.data;
};

export const approveVerification = async (id, remarks = "Verified Successfully") => {
    const response = await api.put(
        `/verifications/${id}/approve`,
        null,
        {
            params: { remarks }
        }
    );

    return response.data;
};

export const rejectVerification = async (id, remarks = "Rejected") => {
    const response = await api.put(
        `/verifications/${id}/reject`,
        null,
        {
            params: { remarks }
        }
    );

    return response.data;
};