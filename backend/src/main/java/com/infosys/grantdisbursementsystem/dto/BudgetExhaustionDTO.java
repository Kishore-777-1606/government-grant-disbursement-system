package com.infosys.grantdisbursementsystem.dto;

public class BudgetExhaustionDTO {

    private String schemeName;
    private Double utilizationPercentage;

    public BudgetExhaustionDTO() {
    }

    public BudgetExhaustionDTO(String schemeName, Double utilizationPercentage) {
        this.schemeName = schemeName;
        this.utilizationPercentage = utilizationPercentage;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Double getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }
}