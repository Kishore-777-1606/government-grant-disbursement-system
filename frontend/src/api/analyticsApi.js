import axios from "axios";


const API = axios.create({

    baseURL:"http://localhost:8080/api"

});


// Dashboard

export const getDashboardSummary = () =>
    API.get("/analytics/dashboard-summary");


// Charts

export const getFundUtilization = () =>
    API.get("/analytics/fund-utilization");


export const getRegionUtilization = () =>
    API.get("/analytics/region-utilization");


export const getCategoryDistribution = () =>
    API.get("/analytics/category-distribution");


export const getBudgetExhaustion = () =>
    API.get("/analytics/budget-exhaustion");


// Other summaries

export const getMilestoneSummary = () =>
    API.get("/analytics/milestone-summary");


export const getApplicationSummary = () =>
    API.get("/analytics/application-summary");


export const getDisbursementSummary = () =>
    API.get("/analytics/disbursement-summary");


// Approval

export const getApprovalTurnaround = () =>
    API.get("/analytics/approval-turnaround");


// Activities

export const getRecentActivities = () =>
    API.get("/analytics/recent-activities");


// Exports (Module 4: downloadable PDF/Excel reports)

export const exportFundUtilizationExcel = () =>
    API.get("/analytics/export/fund-utilization/excel", { responseType: "blob" });

export const exportFundUtilizationPdf = () =>
    API.get("/analytics/export/fund-utilization/pdf", { responseType: "blob" });

export const exportRegionUtilizationExcel = () =>
    API.get("/analytics/export/region-utilization/excel", { responseType: "blob" });

export const exportRegionUtilizationPdf = () =>
    API.get("/analytics/export/region-utilization/pdf", { responseType: "blob" });


export default API;