package com.infosys.grantdisbursementsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "disbursement_installments")
public class DisbursementInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long installmentId;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private DisbursementPlan disbursementPlan;

    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private ComplianceMilestone milestone;

    private Integer installmentNumber;
    private Double installmentAmount;
    private LocalDate scheduledDate;
    private LocalDate actualReleaseDate;
    private String status; // "Scheduled", "Released", "On Hold"

    public DisbursementInstallment() {}

    public Long getInstallmentId() { return installmentId; }
    public void setInstallmentId(Long installmentId) { this.installmentId = installmentId; }
    public DisbursementPlan getDisbursementPlan() { return disbursementPlan; }
    public void setDisbursementPlan(DisbursementPlan disbursementPlan) { this.disbursementPlan = disbursementPlan; }
    public ComplianceMilestone getMilestone() { return milestone; }
    public void setMilestone(ComplianceMilestone milestone) { this.milestone = milestone; }
    public Integer getInstallmentNumber() { return installmentNumber; }
    public void setInstallmentNumber(Integer installmentNumber) { this.installmentNumber = installmentNumber; }
    public Double getInstallmentAmount() { return installmentAmount; }
    public void setInstallmentAmount(Double installmentAmount) { this.installmentAmount = installmentAmount; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public LocalDate getActualReleaseDate() { return actualReleaseDate; }
    public void setActualReleaseDate(LocalDate actualReleaseDate) { this.actualReleaseDate = actualReleaseDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}