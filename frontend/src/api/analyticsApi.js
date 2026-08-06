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


export default API;