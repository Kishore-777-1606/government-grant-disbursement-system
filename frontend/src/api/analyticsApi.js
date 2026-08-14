import api from "../services/api";

// Dashboard
export const getDashboardSummary = () =>
    api.get("/api/analytics/dashboard-summary");

// Charts
export const getFundUtilization = () =>
    api.get("/api/analytics/fund-utilization");

export const getRegionUtilization = () =>
    api.get("/api/analytics/region-utilization");

export const getCategoryDistribution = () =>
    api.get("/api/analytics/category-distribution");

export const getBudgetExhaustion = () =>
    api.get("/api/analytics/budget-exhaustion");

// Other summaries
export const getMilestoneSummary = () =>
    api.get("/api/analytics/milestone-summary");

export const getApplicationSummary = () =>
    api.get("/api/analytics/application-summary");

export const getDisbursementSummary = () =>
    api.get("/api/analytics/disbursement-summary");

// Approval
export const getApprovalTurnaround = () =>
    api.get("/api/analytics/approval-turnaround");

// Activities
export const getRecentActivities = () =>
    api.get("/api/analytics/recent-activities");

// Exports (Module 4: downloadable PDF/Excel reports)
export const exportFundUtilizationExcel = () =>
    api.get("/api/analytics/export/fund-utilization/excel", { responseType: "blob" });

export const exportFundUtilizationPdf = () =>
    api.get("/api/analytics/export/fund-utilization/pdf", { responseType: "blob" });

export const exportRegionUtilizationExcel = () =>
    api.get("/api/analytics/export/region-utilization/excel", { responseType: "blob" });

export const exportRegionUtilizationPdf = () =>
    api.get("/api/analytics/export/region-utilization/pdf", { responseType: "blob" });

export default api;