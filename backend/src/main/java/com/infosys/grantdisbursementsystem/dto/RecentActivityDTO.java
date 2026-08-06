package com.infosys.grantdisbursementsystem.dto;

public class RecentActivityDTO {

    private String activity;
    private String description;


    public RecentActivityDTO(String activity, String description) {
        this.activity = activity;
        this.description = description;
    }


    public String getActivity() {
        return activity;
    }


    public String getDescription() {
        return description;
    }
}