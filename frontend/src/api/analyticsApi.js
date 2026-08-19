import api from "../services/api";

// Dashboard
export const getDashboardSummary = () =>
    api.get("/analytics/dashboard-summary");

// Charts
export const getFundUtilization = () =>
    api.get("/analytics/fund-utilization");

export const getRegionUtilization = () =>
    api.get("/analytics/region-utilization");

export const getCategoryDistribution = () =>
    api.get("/analytics/category-distribution");

export const getBudgetExhaustion = () =>
    api.get("/analytics/budget-exhaustion");

// Other summaries
export const getMilestoneSummary = () =>
    api.get("/analytics/milestone-summary");

export const getApplicationSummary = () =>
    api.get("/analytics/application-summary");

export const getDisbursementSummary = () =>
    api.get("/analytics/disbursement-summary");

// Approval
export const getApprovalTurnaround = () =>
    api.get("/analytics/approval-turnaround");

// Activities
export const getRecentActivities = () =>
    api.get("/analytics/recent-activities");

// Exports (Module 4: downloadable PDF/Excel reports)
export const exportFundUtilizationExcel = () =>
    api.get("/analytics/export/fund-utilization/excel", { responseType: "blob" });

export const exportFundUtilizationPdf = () =>
    api.get("/analytics/export/fund-utilization/pdf", { responseType: "blob" });

export const exportRegionUtilizationExcel = () =>
    api.get("/analytics/export/region-utilization/excel", { responseType: "blob" });

export const exportRegionUtilizationPdf = () =>
    api.get("/analytics/export/region-utilization/pdf", { responseType: "blob" });

export default api;