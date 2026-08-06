package com.infosys.grantdisbursementsystem.dto;

import java.time.LocalDate;

public class RecentActivityDTO {

    private String description;
    private String type;
    private LocalDate date;

    public RecentActivityDTO() {
    }

    public RecentActivityDTO(String description, String type, LocalDate date) {
        this.description = description;
        this.type = type;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}