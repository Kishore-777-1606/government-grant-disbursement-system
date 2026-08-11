package com.infosys.grantdisbursementsystem.dto;

import java.time.LocalDate;

/**
 * Read-only projection of an application's eligibility outcome.
 * Eligibility is not a separate stored entity - the score and status already
 * live on Application (set automatically in ApplicationServiceImpl). This
 * DTO simply presents that data, enriched with beneficiary/scheme names,
 * for the Eligibility screen - avoiding duplicate scoring logic or a
 * duplicate database table.
 */
public class EligibilityView {

    private Long applicationId;
    private Long beneficiaryId;
    private String beneficiaryName;
    private Long schemeId;
    private String schemeName;
    private Double eligibilityScore;
    private String status;
    private LocalDate applicationDate;

    public EligibilityView() {
    }

    public EligibilityView(Long applicationId, Long beneficiaryId, String beneficiaryName,
                            Long schemeId, String schemeName, Double eligibilityScore,
                            String status, LocalDate applicationDate) {
        this.applicationId = applicationId;
        this.beneficiaryId = beneficiaryId;
        this.beneficiaryName = beneficiaryName;
        this.schemeId = schemeId;
        this.schemeName = schemeName;
        this.eligibilityScore = eligibilityScore;
        this.status = status;
        this.applicationDate = applicationDate;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Double getEligibilityScore() {
        return eligibilityScore;
    }

    public void setEligibilityScore(Double eligibilityScore) {
        this.eligibilityScore = eligibilityScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }
}