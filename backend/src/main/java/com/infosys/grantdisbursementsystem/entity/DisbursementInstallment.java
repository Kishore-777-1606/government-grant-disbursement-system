package com.infosys.grantdisbursementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "disbursement_installments")
public class DisbursementInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long installmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnoreProperties({
            "application",
            "hibernateLazyInitializer",
            "handler"
    })
    private DisbursementPlan disbursementPlan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "milestone_id")
    @JsonIgnoreProperties({
            "application",
            "hibernateLazyInitializer",
            "handler"
    })
    private ComplianceMilestone milestone;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "installment_amount")
    private Double installmentAmount;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "actual_release_date")
    private LocalDate actualReleaseDate;

    @Column(name = "status")
    private String status;

    public DisbursementInstallment() {
    }

    public Long getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Long installmentId) {
        this.installmentId = installmentId;
    }

    public DisbursementPlan getDisbursementPlan() {
        return disbursementPlan;
    }

    public void setDisbursementPlan(
            DisbursementPlan disbursementPlan) {

        this.disbursementPlan = disbursementPlan;
    }

    public ComplianceMilestone getMilestone() {
        return milestone;
    }

    public void setMilestone(
            ComplianceMilestone milestone) {

        this.milestone = milestone;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(
            Integer installmentNumber) {

        this.installmentNumber = installmentNumber;
    }

    public Double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(
            Double installmentAmount) {

        this.installmentAmount = installmentAmount;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(
            LocalDate scheduledDate) {

        this.scheduledDate = scheduledDate;
    }

    public LocalDate getActualReleaseDate() {
        return actualReleaseDate;
    }

    public void setActualReleaseDate(
            LocalDate actualReleaseDate) {

        this.actualReleaseDate = actualReleaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}