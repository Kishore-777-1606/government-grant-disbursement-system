package com.infosys.grantdisbursementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "beneficiary")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Beneficiary UID is required")
    @Size(max = 50, message = "Beneficiary UID must not exceed 50 characters")
    @Column(name = "beneficiary_uid", unique = true, nullable = false, length = 50)
    private String beneficiaryUid;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", length = 100)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender")
    private String gender;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "village_id")
    private Long villageId;

    @Column(name = "block_id")
    private Long blockId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "state_id")
    private Long stateId;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9]\\d{5}$", message = "Pincode must be a valid 6-digit Indian pincode")
    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

   @Pattern(regexp = "^$|^[A-Z]{4}0[A-Z0-9]{6}$", message = "IFSC code must be a valid 11-character IFSC code (e.g. SBIN0001234)")
    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "^(General|SC|ST|OBC|EWS)$", message = "Category must be one of General, SC, ST, OBC, EWS")
    @Column(name = "category", length = 20)
    private String category;

    @NotNull(message = "Annual income is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Annual income cannot be negative")
    @Column(name = "annual_income")
    private java.math.BigDecimal annualIncome;

    @Column(name = "disability_status")
    private Boolean disabilityStatus = false;

    // Identity/eligibility proof document (Module 1: "document upload, and
    // identity validation"). documentPath is the name the file is actually
    // stored under on disk (unique, disk-safe); documentOriginalName is what
    // the user uploaded, kept only for display.
    @Column(name = "document_path", length = 255)
    private String documentPath;

    @Column(name = "document_original_name", length = 255)
    private String documentOriginalName;

    @Column(name = "aadhaar_verified")
    private Boolean aadhaarVerified = false;

    @Column(name = "bank_verified")
    private Boolean bankVerified = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}