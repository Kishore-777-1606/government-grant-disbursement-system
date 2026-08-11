package com.infosys.grantdisbursementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheme")
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Scheme code is required")
    @Column(name = "scheme_code", unique = true, nullable = false, length = 20)
    private String schemeCode;

    @NotBlank(message = "Scheme name is required")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "scheme_type")
    private String schemeType;

    @Column(name = "disbursement_mode")
    private String disbursementMode;

    @Column(name = "frequency")
    private String frequency;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    // Eligibility criteria (Module 2 requirement: "configurable scheme criteria").
    // Null max_annual_income means no income ceiling for this scheme.
    @Column(name = "max_annual_income")
    private BigDecimal maxAnnualIncome;

    // Comma-separated beneficiary categories eligible for this scheme
    // (e.g. "SC,ST,OBC"). Null/blank means every category is eligible.
    @Column(name = "allowed_categories", length = 100)
    private String allowedCategories;

    @Column(name = "max_beneficiaries")
    private Integer maxBeneficiaries;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}