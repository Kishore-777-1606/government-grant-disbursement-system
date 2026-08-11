package com.infosys.grantdisbursementsystem.dto;

public class RegionUtilizationDTO {

    private String region;
    private Double totalAmount;

    public RegionUtilizationDTO() {
    }

    public RegionUtilizationDTO(String region, Double totalAmount) {
        this.region = region;
        this.totalAmount = totalAmount;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}