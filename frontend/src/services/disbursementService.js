import api from "./api";

export const getAllPlans = async () => {
    const response = await api.get("/api/disbursement-plans");
    return response.data;
};

export const getAllInstallments = async () => {
    const response = await api.get("/api/disbursement-plans/installments/all");
    return response.data;
};

export const releaseInstallment = async (installmentId) => {
    const response = await api.post(
        `/api/disbursement-plans/release/${installmentId}`
    );
    return response.data;
};

export const completeMilestone = async (milestoneId) => {
    const response = await api.put(`/api/milestones/${milestoneId}/complete`);
    return response.data;
};

export const getMilestoneReminders = async () => {
    const response = await api.get("/api/milestones/reminders");
    return response.data;
};
export const createPlan = async (planData) => {
    const response = await api.post("/api/disbursement-plans", planData);
    return response.data;
};
export const markMilestoneInProgress = async (milestoneId) => {
    const response = await api.put(`/api/milestones/${milestoneId}/in-progress`);
    return response.data;
};

export const markMilestoneNonCompliant = async (milestoneId, reason) => {
    const response = await api.put(
        `/api/milestones/${milestoneId}/non-compliant`,
        null,
        { params: reason ? { reason } : {} }
    );
    return response.data;
};