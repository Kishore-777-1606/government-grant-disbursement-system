package com.infosys.grantdisbursementsystem.dto;

import java.math.BigDecimal;

public class TreasuryReleaseResponse {

    private boolean success;
    private Long beneficiaryId;
    private BigDecimal amount;
    private String referenceId;
    private String message;

    public TreasuryReleaseResponse() {
    }

    public TreasuryReleaseResponse(
            boolean success,
            Long beneficiaryId,
            BigDecimal amount,
            String referenceId,
            String message
    ) {
        this.success = success;
        this.beneficiaryId = beneficiaryId;
        this.amount = amount;
        this.referenceId = referenceId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}