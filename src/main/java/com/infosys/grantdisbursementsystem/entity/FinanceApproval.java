package com.infosys.grantdisbursementsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "finance_approvals")
public class FinanceApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private String approvalStatus;

    @Column(nullable = false)
    private String approvedBy;

    @Column(nullable = false)
    private LocalDate approvalDate;

    @Column(length = 500)
    private String remarks;

    public FinanceApproval() {
    }

    public FinanceApproval(Long approvalId, Application application,
                           String approvalStatus, String approvedBy,
                           LocalDate approvalDate, String remarks) {
        this.approvalId = approvalId;
        this.application = application;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.approvalDate = approvalDate;
        this.remarks = remarks;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}