package com.infosys.grantdisbursementsystem.dto;

public class ApplicationSummaryDTO {

    private long totalApplications;
    private long approvedApplications;
    private long pendingApplications;
    private long rejectedApplications;

    public ApplicationSummaryDTO() {
    }

    public ApplicationSummaryDTO(long totalApplications,
                                 long approvedApplications,
                                 long pendingApplications,
                                 long rejectedApplications) {
        this.totalApplications = totalApplications;
        this.approvedApplications = approvedApplications;
        this.pendingApplications = pendingApplications;
        this.rejectedApplications = rejectedApplications;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getApprovedApplications() {
        return approvedApplications;
    }

    public void setApprovedApplications(long approvedApplications) {
        this.approvedApplications = approvedApplications;
    }

    public long getPendingApplications() {
        return pendingApplications;
    }

    public void setPendingApplications(long pendingApplications) {
        this.pendingApplications = pendingApplications;
    }

    public long getRejectedApplications() {
        return rejectedApplications;
    }

    public void setRejectedApplications(long rejectedApplications) {
        this.rejectedApplications = rejectedApplications;
    }
}
