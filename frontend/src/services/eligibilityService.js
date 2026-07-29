import api from "./api";

export const getAllEligibilityRecords = async () => {
    const response = await api.get("/eligibility");
    return response.data;
};

export const getEligibilityByApplicationId = async (applicationId) => {
    const response = await api.get(`/eligibility/${applicationId}`);
    return response.data;
};
