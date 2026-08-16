package com.infosys.grantdisbursementsystem.dto;

public class BeneficiaryVerificationResponse {

    private Long beneficiaryId;
    private boolean verified;
    private String message;

    public BeneficiaryVerificationResponse() {
    }

    public BeneficiaryVerificationResponse(
            Long beneficiaryId,
            boolean verified,
            String message
    ) {
        this.beneficiaryId = beneficiaryId;
        this.verified = verified;
        this.message = message;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}