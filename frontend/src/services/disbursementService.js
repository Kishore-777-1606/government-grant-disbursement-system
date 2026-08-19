import api from "./api";

// =====================================================
// GET ALL DISBURSEMENT PLANS
// =====================================================

export const getAllPlans = async () => {
    const response = await api.get("/disbursement-plans");
    return response.data;
};


// =====================================================
// GET ONE DISBURSEMENT PLAN
// =====================================================

export const getPlanById = async (planId) => {
    const response = await api.get(
        `/disbursement-plans/${planId}`
    );

    return response.data;
};


// =====================================================
// CREATE DISBURSEMENT PLAN
// =====================================================

export const createDisbursementPlan = async (
    applicationId,
    totalAmount,
    numberOfInstallments
) => {

    const response = await api.post(
        "/disbursement-plans",
        {
            applicationId: Number(applicationId),
            totalAmount: Number(totalAmount),
            numberOfInstallments: Number(numberOfInstallments)
        }
    );

    return response.data;
};


// =====================================================
// GET ALL INSTALLMENTS
// =====================================================

export const getAllInstallments = async () => {

    const response = await api.get(
        "/disbursement-plans/installments/all"
    );

    return response.data;
};


// =====================================================
// GET INSTALLMENTS BY PLAN
// =====================================================

export const getInstallmentsByPlan = async (planId) => {

    const response = await api.get(
        `/disbursement-plans/${planId}/installments`
    );

    return response.data;
};


// =====================================================
// RELEASE INSTALLMENT
// =====================================================

export const releaseInstallment = async (
    installmentId
) => {

    const response = await api.post(
        `/disbursement-plans/release/${installmentId}`
    );

    return response.data;
};


// =====================================================
// COMPLETE MILESTONE
// =====================================================

export const completeMilestone = async (
    milestoneId
) => {

    const response = await api.put(
        `/milestones/${milestoneId}/complete`
    );

    return response.data;
};


// =====================================================
// GET MILESTONE REMINDERS
// =====================================================

export const getMilestoneReminders = async () => {

    const response = await api.get(
        "/milestones/reminders"
    );

    return response.data;
};


// =====================================================
// CHECK OVERDUE MILESTONES
// =====================================================

export const checkOverdueMilestones = async () => {

    const response = await api.get(
        "/milestones/check-overdue"
    );

    return response.data;
};


// =====================================================
// MARK MILESTONE IN PROGRESS
// =====================================================

export const markMilestoneInProgress = async (milestoneId) => {
    const response = await api.put(`/milestones/${milestoneId}/in-progress`);
    return response.data;
};


// =====================================================
// MARK MILESTONE NON-COMPLIANT
// =====================================================

export const markMilestoneNonCompliant = async (milestoneId, reason) => {
    const response = await api.put(
        `/milestones/${milestoneId}/non-compliant`,
        null,
        { params: reason ? { reason } : {} }
    );
    return response.data;
};