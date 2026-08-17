import axiosInstance from "./axiosInstance";

// Dashboard
export const getDashboardSummary = () =>
    axiosInstance.get("/analytics/dashboard-summary");

// Charts
export const getFundUtilization = () =>
    axiosInstance.get("/analytics/fund-utilization");

export const getRegionUtilization = () =>
    axiosInstance.get("/analytics/region-utilization");

export const getCategoryDistribution = () =>
    axiosInstance.get("/analytics/category-distribution");

export const getBudgetExhaustion = () =>
    axiosInstance.get("/analytics/budget-exhaustion");

// Other summaries
export const getMilestoneSummary = () =>
    axiosInstance.get("/analytics/milestone-summary");

export const getApplicationSummary = () =>
    axiosInstance.get("/analytics/application-summary");

export const getDisbursementSummary = () =>
    axiosInstance.get("/analytics/disbursement-summary");

// Approval
export const getApprovalTurnaround = () =>
    axiosInstance.get("/analytics/approval-turnaround");

// Activities
export const getRecentActivities = () =>
    axiosInstance.get("/analytics/recent-activities");

// Exports
export const exportFundUtilizationExcel = () =>
    axiosInstance.get(
        "/analytics/export/fund-utilization/excel",
        { responseType: "blob" }
    );

export const exportFundUtilizationPdf = () =>
    axiosInstance.get(
        "/analytics/export/fund-utilization/pdf",
        { responseType: "blob" }
    );

export const exportRegionUtilizationExcel = () =>
    axiosInstance.get(
        "/analytics/export/region-utilization/excel",
        { responseType: "blob" }
    );

export const exportRegionUtilizationPdf = () =>
    axiosInstance.get(
        "/analytics/export/region-utilization/pdf",
        { responseType: "blob" }
    );

export default axiosInstance;