import api from "./api";

export const getAllVerifications = async () => {
    const response = await api.get("/verifications");
    return response.data;
};

export const approveVerification = async (id, role, remarks = "Verified Successfully") => {
    const response = await api.put(
        `/verifications/${id}/approve`,
        null,
        {
            params: {
                remarks,
                role
            }
        }
    );

    return response.data;
};

export const rejectVerification = async (id, role, remarks = "Rejected") => {
    const response = await api.put(
        `/verifications/${id}/reject`,
        null,
        {
            params: {
                remarks,
                role
            }
        }
    );

    return response.data;
};
