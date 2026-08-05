package com.infosys.grantdisbursementsystem.dto;

public class FundUtilizationDTO {

    private String schemeName;
    private Double totalAmount;
    private Double releasedAmount;
    private Double remainingAmount;

    public FundUtilizationDTO() {
    }

    public FundUtilizationDTO(String schemeName,
                              Double totalAmount,
                              Double releasedAmount,
                              Double remainingAmount) {
        this.schemeName = schemeName;
        this.totalAmount = totalAmount;
        this.releasedAmount = releasedAmount;
        this.remainingAmount = remainingAmount;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getReleasedAmount() {
        return releasedAmount;
    }

    public void setReleasedAmount(Double releasedAmount) {
        this.releasedAmount = releasedAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}