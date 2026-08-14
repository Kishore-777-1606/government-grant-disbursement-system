import api from "./api";

export const getAllApprovals = async () => {
    const response = await api.get("/finance");
    return response.data;
};

export const approveFinance = async (id) => {
    const response = await api.put(
        `/finance/${id}/approve`,
        null,
        {
            params: {
                remarks: "Payment Approved",
            }
        }
    );

    return response.data;
};

export const rejectFinance = async (id) => {
    const response = await api.put(
        `/finance/${id}/reject`,
        null,
        {
            params: {
                remarks: "Payment Rejected",
            }
        }
    );

    return response.data;
};