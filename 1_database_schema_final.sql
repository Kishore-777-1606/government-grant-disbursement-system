-- ============================================================================
-- GOVERNMENT BENEFICIARY DISBURSEMENT SYSTEM
-- Database Schema Design
-- 
-- Description: This script creates the complete database schema for the
--              Government Beneficiary Disbursement System. It includes all
--              table definitions, primary keys, foreign keys, constraints,
--              and relationships following 3NF normalization standards.
--
-- Compatible With: MySQL 8.0+
-- Author: Database & Eligibility Scoring Module
-- Version: 1.0
-- ============================================================================


-- ============================================================================
-- SECTION 1: DATABASE CREATION
-- ============================================================================
-- This section creates the database and sets it as the active database
-- for all subsequent operations.

DROP DATABASE IF EXISTS gov_beneficiary_db;

CREATE DATABASE gov_beneficiary_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gov_beneficiary_db;

-- ============================================================================
-- SECTION 2: REFERENCE / LOOKUP TABLES
-- ============================================================================
-- Reference tables store enumeration values and lookup data.
-- These tables support data integrity and are referenced by other tables
-- through foreign key relationships.


-- ============================================================================
-- Table: roles
-- Purpose: Stores system user roles and their access level descriptions
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE roles (
    role_id         INT             NOT NULL AUTO_INCREMENT,
    role_name       VARCHAR(50)     NOT NULL,
    role_description VARCHAR(255)   NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (role_id),
    
    -- Role names must be unique to prevent duplicate role definitions
    CONSTRAINT uq_role_name UNIQUE (role_name),
    
    -- Validate role_name is not empty
    CONSTRAINT chk_role_name_not_empty CHECK (CHAR_LENGTH(TRIM(role_name)) > 0)
) ENGINE=InnoDB COMMENT='System user roles and access levels';


-- ============================================================================
-- Table: states
-- Purpose: Stores state/union territory information
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE states (
    state_id        INT             NOT NULL AUTO_INCREMENT,
    state_code      VARCHAR(10)     NOT NULL,
    state_name      VARCHAR(100)    NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (state_id),
    
    -- State codes (e.g., MH, KA, DL) must be unique
    CONSTRAINT uq_state_code UNIQUE (state_code),
    
    -- State names must be unique
    CONSTRAINT uq_state_name UNIQUE (state_name),
    
    -- Validate state_code format (2-10 characters, uppercase)
    CONSTRAINT chk_state_code_format CHECK (state_code REGEXP '^[A-Z]{2,10}$'),
    
    -- Validate state_name is not empty
    CONSTRAINT chk_state_name_not_empty CHECK (CHAR_LENGTH(TRIM(state_name)) > 0)
) ENGINE=InnoDB COMMENT='State and Union Territory reference data';


-- ============================================================================
-- Table: districts
-- Purpose: Stores district information linked to states
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE districts (
    district_id     INT             NOT NULL AUTO_INCREMENT,
    state_id        INT             NOT NULL,
    district_code   VARCHAR(10)     NOT NULL,
    district_name   VARCHAR(100)    NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (district_id),
    
    -- Foreign key relationship: districts belong to a state
    CONSTRAINT fk_districts_state
        FOREIGN KEY (state_id) REFERENCES states(state_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- District codes must be unique within the system
    CONSTRAINT uq_district_code UNIQUE (district_code),
    
    -- Unique combination of state and district name
    CONSTRAINT uq_state_district_name UNIQUE (state_id, district_name),
    
    -- Validate district_code format
    CONSTRAINT chk_district_code_format CHECK (district_code REGEXP '^[A-Z0-9]{2,10}$'),
    
    -- Validate district_name is not empty
    CONSTRAINT chk_district_name_not_empty CHECK (CHAR_LENGTH(TRIM(district_name)) > 0)
) ENGINE=InnoDB COMMENT='District-level administrative regions';


-- ============================================================================
-- Table: blocks
-- Purpose: Stores block/taluka information linked to districts
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE blocks (
    block_id        INT             NOT NULL AUTO_INCREMENT,
    district_id     INT             NOT NULL,
    block_code      VARCHAR(10)     NOT NULL,
    block_name      VARCHAR(100)    NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (block_id),
    
    -- Foreign key relationship: blocks belong to a district
    CONSTRAINT fk_blocks_district
        FOREIGN KEY (district_id) REFERENCES districts(district_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Block codes must be unique within the system
    CONSTRAINT uq_block_code UNIQUE (block_code),
    
    -- Unique combination of district and block name
    CONSTRAINT uq_district_block_name UNIQUE (district_id, block_name),
    
    -- Validate block_code format
    CONSTRAINT chk_block_code_format CHECK (block_code REGEXP '^[A-Z0-9]{2,10}$'),
    
    -- Validate block_name is not empty
    CONSTRAINT chk_block_name_not_empty CHECK (CHAR_LENGTH(TRIM(block_name)) > 0)
) ENGINE=InnoDB COMMENT='Block/Taluka-level administrative regions';


-- ============================================================================
-- Table: villages
-- Purpose: Stores village/ward information linked to blocks
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE villages (
    village_id      INT             NOT NULL AUTO_INCREMENT,
    block_id        INT             NOT NULL,
    village_code    VARCHAR(10)     NOT NULL,
    village_name    VARCHAR(100)    NOT NULL,
    pin_code        VARCHAR(6)      NULL,
    population      INT             NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (village_id),
    
    -- Foreign key relationship: villages belong to a block
    CONSTRAINT fk_villages_block
        FOREIGN KEY (block_id) REFERENCES blocks(block_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Village codes must be unique within the system
    CONSTRAINT uq_village_code UNIQUE (village_code),
    
    -- Unique combination of block and village name
    CONSTRAINT uq_block_village_name UNIQUE (block_id, village_name),
    
    -- Validate pin_code is 6 digits (Indian PIN code format)
    CONSTRAINT chk_pin_code_format CHECK (pin_code IS NULL OR pin_code REGEXP '^[0-9]{6}$'),
    
    -- Validate population is positive when provided
    CONSTRAINT chk_population_positive CHECK (population IS NULL OR population >= 0),
    
    -- Validate village_name is not empty
    CONSTRAINT chk_village_name_not_empty CHECK (CHAR_LENGTH(TRIM(village_name)) > 0)
) ENGINE=InnoDB COMMENT='Village/Ward-level administrative regions';


-- ============================================================================
-- SECTION 3: USER MANAGEMENT TABLES
-- ============================================================================
-- These tables manage system users and their role assignments.


-- ============================================================================
-- Table: users
-- Purpose: Stores system user accounts (admins, officers, verifiers)
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE users (
    user_id         INT             NOT NULL AUTO_INCREMENT,
    role_id         INT             NOT NULL,
    village_id      INT             NULL,
    first_name      VARCHAR(50)     NOT NULL,
    last_name       VARCHAR(50)     NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    phone_number    VARCHAR(15)     NOT NULL,
    username        VARCHAR(50)     NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    employee_code   VARCHAR(20)     NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMP       NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id),
    
    -- Foreign key relationship: users have a role
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Foreign key relationship: users can be assigned to a village
    CONSTRAINT fk_users_village
        FOREIGN KEY (village_id) REFERENCES villages(village_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    
    -- Email must be unique across all users
    CONSTRAINT uq_user_email UNIQUE (email),
    
    -- Username must be unique across all users
    CONSTRAINT uq_user_username UNIQUE (username),
    
    -- Phone number must be unique across all users
    CONSTRAINT uq_user_phone UNIQUE (phone_number),
    
    -- Validate email format using a basic pattern
    CONSTRAINT chk_email_format CHECK (
        email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'
    ),
    
    -- Validate phone number format (10-15 digits with optional + prefix)
    CONSTRAINT chk_phone_format CHECK (
        phone_number REGEXP '^[+]?[0-9]{10,15}$'
    ),
    
    -- Validate first_name is not empty
    CONSTRAINT chk_first_name_not_empty CHECK (CHAR_LENGTH(TRIM(first_name)) > 0),
    
    -- Validate last_name is not empty
    CONSTRAINT chk_last_name_not_empty CHECK (CHAR_LENGTH(TRIM(last_name)) > 0),
    
    -- Validate username is not empty
    CONSTRAINT chk_username_not_empty CHECK (CHAR_LENGTH(TRIM(username)) > 0)
) ENGINE=InnoDB COMMENT='System user accounts and credentials';


-- ============================================================================
-- SECTION 4: BENEFICIARY AND SCHEME TABLES
-- ============================================================================
-- These tables manage beneficiary registration and government scheme definitions.


-- ============================================================================
-- Table: beneficiaries
-- Purpose: Stores beneficiary personal and demographic information
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE beneficiaries (
    beneficiary_id      INT             NOT NULL AUTO_INCREMENT,
    village_id          INT             NOT NULL,
    first_name          VARCHAR(50)     NOT NULL,
    last_name           VARCHAR(50)     NOT NULL,
    date_of_birth       DATE            NOT NULL,
    gender              ENUM('Male', 'Female', 'Other') NOT NULL,
    aadhaar_number      VARCHAR(12)     NOT NULL,
    pan_number          VARCHAR(10)     NULL,
    phone_number        VARCHAR(15)     NOT NULL,
    email               VARCHAR(100)    NULL,
    address_line1       VARCHAR(255)    NOT NULL,
    address_line2       VARCHAR(255)    NULL,
    pin_code            VARCHAR(6)      NULL,
    annual_income       DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    category            ENUM('General', 'SC', 'ST', 'OBC', 'EWS') NOT NULL DEFAULT 'General',
    disability_status   BOOLEAN         NOT NULL DEFAULT FALSE,
    is_bpl              BOOLEAN         NOT NULL DEFAULT FALSE,
    bank_account_number VARCHAR(20)     NULL,
    ifsc_code           VARCHAR(11)     NULL,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (beneficiary_id),
    
    -- Foreign key relationship: beneficiaries reside in a village
    CONSTRAINT fk_beneficiaries_village
        FOREIGN KEY (village_id) REFERENCES villages(village_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Aadhaar number must be unique (12-digit Indian national ID)
    CONSTRAINT uq_beneficiary_aadhaar UNIQUE (aadhaar_number),
    
    -- PAN number must be unique when provided
    CONSTRAINT uq_beneficiary_pan UNIQUE (pan_number),
    
    -- Phone number must be unique
    CONSTRAINT uq_beneficiary_phone UNIQUE (phone_number),
    
    -- Email must be unique when provided
    CONSTRAINT uq_beneficiary_email UNIQUE (email),
    
    -- Validate Aadhaar number is exactly 12 digits
    CONSTRAINT chk_aadhaar_format CHECK (
        aadhaar_number REGEXP '^[0-9]{12}$'
    ),
    
    -- Validate PAN number format (optional, 10 alphanumeric)
    CONSTRAINT chk_pan_format CHECK (
        pan_number IS NULL OR pan_number REGEXP '^[A-Z]{5}[0-9]{4}[A-Z]{1}$'
    ),
    
    -- Validate phone number format
    CONSTRAINT chk_beneficiary_phone_format CHECK (
        phone_number REGEXP '^[+]?[0-9]{10,15}$'
    ),
    
    -- Validate date of birth is not in the future
        
    -- Validate annual income is non-negative
    CONSTRAINT chk_annual_income_non_negative CHECK (annual_income >= 0),
    
    -- Validate IFSC code format when provided (11 characters)
    CONSTRAINT chk_ifsc_format CHECK (
        ifsc_code IS NULL OR ifsc_code REGEXP '^[A-Z]{4}0[A-Z0-9]{6}$'
    ),
    
    -- Validate first_name and last_name are not empty
    CONSTRAINT chk_beneficiary_first_name CHECK (CHAR_LENGTH(TRIM(first_name)) > 0),
    CONSTRAINT chk_beneficiary_last_name CHECK (CHAR_LENGTH(TRIM(last_name)) > 0)
) ENGINE=InnoDB COMMENT='Government beneficiary personal and demographic information';


-- ============================================================================
-- Table: schemes
-- Purpose: Stores government scheme definitions and eligibility criteria
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE schemes (
    scheme_id           INT             NOT NULL AUTO_INCREMENT,
    scheme_code         VARCHAR(20)     NOT NULL,
    scheme_name         VARCHAR(200)    NOT NULL,
    scheme_description  TEXT            NULL,
    scheme_category     ENUM('Agriculture', 'Education', 'Healthcare', 'Housing', 'Employment', 'Social Welfare', 'Pension', 'Subsidy') NOT NULL,
    min_age             INT             NULL,
    max_age             INT             NULL,
    min_annual_income   DECIMAL(12,2)   NULL DEFAULT 0.00,
    max_annual_income   DECIMAL(12,2)   NULL,
    eligible_categories VARCHAR(255)    NULL DEFAULT 'General,SC,ST,OBC,EWS',
    requires_disability BOOLEAN         NOT NULL DEFAULT FALSE,
    requires_bpl        BOOLEAN         NOT NULL DEFAULT FALSE,
    max_disbursement_amount DECIMAL(12,2) NOT NULL,
    installment_count   INT             NOT NULL DEFAULT 1,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    effective_from      DATE            NOT NULL,
    effective_to        DATE            NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (scheme_id),
    
    -- Scheme codes must be unique
    CONSTRAINT uq_scheme_code UNIQUE (scheme_code),
    
    -- Scheme names must be unique
    CONSTRAINT uq_scheme_name UNIQUE (scheme_name),
    
    -- Validate age constraints
    CONSTRAINT chk_scheme_age_range CHECK (
        (min_age IS NULL AND max_age IS NULL) OR
        (min_age IS NOT NULL AND max_age IS NOT NULL AND min_age <= max_age) OR
        (min_age IS NOT NULL AND max_age IS NULL) OR
        (min_age IS NULL AND max_age IS NOT NULL)
    ),
    
    -- Validate income constraints
    CONSTRAINT chk_scheme_income_range CHECK (
        (min_annual_income IS NULL OR max_annual_income IS NULL) OR
        (min_annual_income <= max_annual_income)
    ),
    
    -- Validate disbursement amount is positive
    CONSTRAINT chk_disbursement_positive CHECK (max_disbursement_amount > 0),
    
    -- Validate installment count is positive
    CONSTRAINT chk_installment_positive CHECK (installment_count >= 1),
    
    -- Validate effective_from is not after effective_to
    CONSTRAINT chk_effective_dates CHECK (
        effective_to IS NULL OR effective_from <= effective_to
    ),
    
    -- Validate scheme_code is not empty
    CONSTRAINT chk_scheme_code_not_empty CHECK (CHAR_LENGTH(TRIM(scheme_code)) > 0),
    
    -- Validate scheme_name is not empty
    CONSTRAINT chk_scheme_name_not_empty CHECK (CHAR_LENGTH(TRIM(scheme_name)) > 0)
) ENGINE=InnoDB COMMENT='Government welfare scheme definitions and eligibility criteria';


-- ============================================================================
-- SECTION 5: APPLICATION AND PROCESSING TABLES
-- ============================================================================
-- These tables manage the application lifecycle from submission to disbursement.


-- ============================================================================
-- Table: applications
-- Purpose: Stores beneficiary scheme applications and their status
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE applications (
    application_id      INT             NOT NULL AUTO_INCREMENT,
    beneficiary_id      INT             NOT NULL,
    scheme_id           INT             NOT NULL,
    application_number  VARCHAR(30)     NOT NULL,
    application_date    DATE            NOT NULL,
    application_status  ENUM('Submitted', 'Under Review', 'Eligibility Checked', 'Verified', 'Approved', 'Rejected', 'Disbursed', 'Cancelled') NOT NULL DEFAULT 'Submitted',
    applied_amount      DECIMAL(12,2)   NOT NULL,
    approved_amount     DECIMAL(12,2)   NULL,
    rejection_reason    TEXT            NULL,
    reviewed_by         INT             NULL,
    reviewed_at         TIMESTAMP       NULL,
    remarks             TEXT            NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (application_id),
    
    -- Foreign key relationship: applications are submitted by beneficiaries
    CONSTRAINT fk_applications_beneficiary
        FOREIGN KEY (beneficiary_id) REFERENCES beneficiaries(beneficiary_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Foreign key relationship: applications are for a specific scheme
    CONSTRAINT fk_applications_scheme
        FOREIGN KEY (scheme_id) REFERENCES schemes(scheme_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Foreign key relationship: applications are reviewed by a user
    CONSTRAINT fk_applications_reviewed_by
        FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    
    -- Application numbers must be unique
    CONSTRAINT uq_application_number UNIQUE (application_number),
    
    -- Unique combination: one application per beneficiary per scheme
    CONSTRAINT uq_beneficiary_scheme_application UNIQUE (beneficiary_id, scheme_id),
    
    -- Validate application_number format (APP-YYYY-NNNNNN)
    CONSTRAINT chk_application_number_format CHECK (
        application_number REGEXP '^APP-[0-9]{4}-[0-9]{6}$'
    ),
    
    -- Validate applied_amount is positive
    CONSTRAINT chk_applied_amount_positive CHECK (applied_amount > 0),
    
    -- Validate approved_amount is non-negative when provided
    CONSTRAINT chk_approved_amount_non_negative CHECK (
        approved_amount IS NULL OR approved_amount >= 0
    ),
    
    -- Validate application_date is not in the future
-- Validate application date
CONSTRAINT chk_application_date_valid
CHECK (application_date >= '2000-01-01')
) ENGINE=InnoDB COMMENT='Beneficiary scheme applications and status tracking';


-- ============================================================================
-- Table: eligibility
-- Purpose: Stores eligibility evaluation results for applications
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE eligibility (
    eligibility_id          INT             NOT NULL AUTO_INCREMENT,
    application_id          INT             NOT NULL,
    evaluated_by            INT             NULL,
    evaluation_date         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    age_score               INT             NOT NULL DEFAULT 0,
    income_score            INT             NOT NULL DEFAULT 0,
    category_score          INT             NOT NULL DEFAULT 0,
    disability_score        INT             NOT NULL DEFAULT 0,
    bpl_score               INT             NOT NULL DEFAULT 0,
    location_score          INT             NOT NULL DEFAULT 0,
    total_eligibility_score INT             NOT NULL DEFAULT 0,
    max_possible_score      INT             NOT NULL DEFAULT 100,
    eligibility_status      ENUM('Eligible', 'Partially Eligible', 'Not Eligible', 'Pending Review') NOT NULL DEFAULT 'Pending Review',
    evaluation_notes        TEXT            NULL,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (eligibility_id),
    
    -- Foreign key relationship: eligibility is evaluated for an application
    CONSTRAINT fk_eligibility_application
        FOREIGN KEY (application_id) REFERENCES applications(application_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Foreign key relationship: eligibility is evaluated by a user
    CONSTRAINT fk_eligibility_evaluated_by
        FOREIGN KEY (evaluated_by) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    
    -- One eligibility record per application
    CONSTRAINT uq_eligibility_application UNIQUE (application_id),
    
    -- Validate individual scores are non-negative
    CONSTRAINT chk_age_score_non_negative CHECK (age_score >= 0),
    CONSTRAINT chk_income_score_non_negative CHECK (income_score >= 0),
    CONSTRAINT chk_category_score_non_negative CHECK (category_score >= 0),
    CONSTRAINT chk_disability_score_non_negative CHECK (disability_score >= 0),
    CONSTRAINT chk_bpl_score_non_negative CHECK (bpl_score >= 0),
    CONSTRAINT chk_location_score_non_negative CHECK (location_score >= 0),
    
    -- Validate total score is non-negative
    CONSTRAINT chk_total_score_non_negative CHECK (total_eligibility_score >= 0),
    
    -- Validate max_possible_score is positive
    CONSTRAINT chk_max_possible_score_positive CHECK (max_possible_score > 0),
    
    -- Validate total score does not exceed max possible score
    CONSTRAINT chk_score_within_limit CHECK (total_eligibility_score <= max_possible_score)
) ENGINE=InnoDB COMMENT='Eligibility evaluation results and scoring breakdown';


-- ============================================================================
-- Table: verification
-- Purpose: Stores document and field verification records
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE verification (
    verification_id     INT             NOT NULL AUTO_INCREMENT,
    application_id      INT             NOT NULL,
    verifier_id         INT             NULL,
    verification_type   ENUM('Document', 'Field Visit', 'Aadhaar Authentication', 'Income Verification', 'Address Verification') NOT NULL,
    verification_status ENUM('Pending', 'In Progress', 'Passed', 'Failed', 'Requires Re-verification') NOT NULL DEFAULT 'Pending',
    verification_date   TIMESTAMP       NULL,
    document_verified   BOOLEAN         NOT NULL DEFAULT FALSE,
    field_visit_done    BOOLEAN         NOT NULL DEFAULT FALSE,
    aadhaar_verified    BOOLEAN         NOT NULL DEFAULT FALSE,
    income_verified     BOOLEAN         NOT NULL DEFAULT FALSE,
    address_verified    BOOLEAN         NOT NULL DEFAULT FALSE,
    verification_notes  TEXT            NULL,
    supporting_documents TEXT           NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (verification_id),
    
    -- Foreign key relationship: verification is done for an application
    CONSTRAINT fk_verification_application
        FOREIGN KEY (application_id) REFERENCES applications(application_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Foreign key relationship: verification is performed by a verifier
    CONSTRAINT fk_verification_verifier
        FOREIGN KEY (verifier_id) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    
    -- One verification record per application
    CONSTRAINT uq_verification_application UNIQUE (application_id),
    
    -- Validate verification_date is not before creation
    CONSTRAINT chk_verification_date_valid CHECK (
        verification_date IS NULL OR verification_date >= created_at
    )
) ENGINE=InnoDB COMMENT='Application verification and document authentication records';


-- ============================================================================
-- Table: disbursement
-- Purpose: Stores payment disbursement records and transaction details
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE disbursement (
    disbursement_id         INT             NOT NULL AUTO_INCREMENT,
    application_id          INT             NOT NULL,
    beneficiary_id          INT             NOT NULL,
    disbursement_number     VARCHAR(30)     NOT NULL,
    disbursement_date       DATE            NULL,
    disbursement_amount     DECIMAL(12,2)   NOT NULL,
    disbursement_mode       ENUM('Direct Bank Transfer', 'DBT', 'Cheque', 'Cash', 'UPI') NOT NULL DEFAULT 'Direct Bank Transfer',
    disbursement_status     ENUM('Pending', 'Processing', 'Completed', 'Failed', 'Reversed') NOT NULL DEFAULT 'Pending',
    bank_account_number     VARCHAR(20)     NOT NULL,
    ifsc_code               VARCHAR(11)     NOT NULL,
    transaction_reference   VARCHAR(50)     NULL,
    authorized_by           INT             NULL,
    authorized_at           TIMESTAMP       NULL,
    failure_reason          TEXT            NULL,
    installment_number      INT             NOT NULL DEFAULT 1,
    total_installments      INT             NOT NULL DEFAULT 1,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (disbursement_id),
    
    -- Foreign key relationship: disbursement is for an application
    CONSTRAINT fk_disbursement_application
        FOREIGN KEY (application_id) REFERENCES applications(application_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Foreign key relationship: disbursement is made to a beneficiary
    CONSTRAINT fk_disbursement_beneficiary
        FOREIGN KEY (beneficiary_id) REFERENCES beneficiaries(beneficiary_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    
    -- Foreign key relationship: disbursement is authorized by a user
    CONSTRAINT fk_disbursement_authorized_by
        FOREIGN KEY (authorized_by) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    
    -- Disbursement numbers must be unique
    CONSTRAINT uq_disbursement_number UNIQUE (disbursement_number),
    
    -- Validate disbursement_amount is positive
    CONSTRAINT chk_disbursement_amount_positive CHECK (disbursement_amount > 0),
    
    -- Validate installment numbers are positive
    CONSTRAINT chk_installment_number_positive CHECK (installment_number >= 1),
    CONSTRAINT chk_total_installments_positive CHECK (total_installments >= 1),
    
    -- Validate installment_number does not exceed total_installments
    CONSTRAINT chk_installment_within_total CHECK (installment_number <= total_installments),
    
    -- Validate bank_account_number is not empty
    CONSTRAINT chk_bank_account_not_empty CHECK (CHAR_LENGTH(TRIM(bank_account_number)) > 0),
    
    -- Validate ifsc_code format
    CONSTRAINT chk_disbursement_ifsc_format CHECK (
        ifsc_code REGEXP '^[A-Z]{4}0[A-Z0-9]{6}$'
    ),
    
    -- Validate disbursement_number format (DISB-YYYY-NNNNNN)
    CONSTRAINT chk_disbursement_number_format CHECK (
        disbursement_number REGEXP '^DISB-[0-9]{4}-[0-9]{6}$'
    )
) ENGINE=InnoDB COMMENT='Payment disbursement records and transaction tracking';


-- ============================================================================
-- Table: audit_log
-- Purpose: Stores complete audit trail of all system activities
-- Normalization: Yes - 3NF compliant
-- ============================================================================

CREATE TABLE audit_log (
    log_id              INT             NOT NULL AUTO_INCREMENT,
    user_id             INT             NULL,
    action_type         ENUM('INSERT', 'UPDATE', 'DELETE', 'SELECT', 'LOGIN', 'LOGOUT', 'APPROVE', 'REJECT', 'DISBURSE', 'VERIFY') NOT NULL,
    table_affected      VARCHAR(50)     NOT NULL,
    record_id           INT             NULL,
    old_values          JSON            NULL,
    new_values          JSON            NULL,
    ip_address          VARCHAR(45)     NULL,
    user_agent          VARCHAR(255)    NULL,
    action_description  TEXT            NULL,
    action_timestamp    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (log_id),
    
    -- Foreign key relationship: audit log entry is made by a user
    CONSTRAINT fk_audit_log_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    
    -- Validate table_affected is not empty
    CONSTRAINT chk_table_affected_not_empty CHECK (CHAR_LENGTH(TRIM(table_affected)) > 0),
    
    -- Validate IP address format when provided (IPv4 or IPv6)
    CONSTRAINT chk_ip_address_format CHECK (
        ip_address IS NULL OR 
        ip_address REGEXP '^([0-9]{1,3}\\.){3}[0-9]{1,3}$' OR
        ip_address REGEXP '^[0-9a-fA-F:]+$'
    )
) ENGINE=InnoDB COMMENT='Complete audit trail of all system activities';


-- ============================================================================
-- SECTION 6: TRIGGERS FOR AUTOMATED AUDIT LOGGING
-- ============================================================================
-- Triggers automatically capture changes for audit purposes.


-- ============================================================================
-- Trigger: trg_beneficiaries_after_insert
-- Purpose: Log new beneficiary registrations
-- ============================================================================

DELIMITER //

CREATE TRIGGER trg_beneficiaries_after_insert
AFTER INSERT ON beneficiaries
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        action_type, table_affected, record_id, new_values, action_description
    ) VALUES (
        'INSERT', 'beneficiaries', NEW.beneficiary_id,
        JSON_OBJECT(
            'beneficiary_id', NEW.beneficiary_id,
            'first_name', NEW.first_name,
            'last_name', NEW.last_name,
            'aadhaar_number', NEW.aadhaar_number,
            'village_id', NEW.village_id
        ),
        CONCAT('New beneficiary registered: ', NEW.first_name, ' ', NEW.last_name)
    );
END //

DELIMITER ;


-- ============================================================================
-- Trigger: trg_applications_after_insert
-- Purpose: Log new application submissions
-- ============================================================================

DELIMITER //

CREATE TRIGGER trg_applications_after_insert
AFTER INSERT ON applications
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        action_type, table_affected, record_id, new_values, action_description
    ) VALUES (
        'INSERT', 'applications', NEW.application_id,
        JSON_OBJECT(
            'application_id', NEW.application_id,
            'beneficiary_id', NEW.beneficiary_id,
            'scheme_id', NEW.scheme_id,
            'application_number', NEW.application_number,
            'application_status', NEW.application_status
        ),
        CONCAT('New application submitted: ', NEW.application_number)
    );
END //

DELIMITER ;


-- ============================================================================
-- Trigger: trg_applications_after_update
-- Purpose: Log application status changes
-- ============================================================================

DELIMITER //

CREATE TRIGGER trg_applications_after_update
AFTER UPDATE ON applications
FOR EACH ROW
BEGIN
    IF OLD.application_status != NEW.application_status THEN
        INSERT INTO audit_log (
            action_type, table_affected, record_id, old_values, new_values, action_description
        ) VALUES (
            'UPDATE', 'applications', NEW.application_id,
            JSON_OBJECT('application_status', OLD.application_status, 'approved_amount', OLD.approved_amount),
            JSON_OBJECT('application_status', NEW.application_status, 'approved_amount', NEW.approved_amount),
            CONCAT('Application ', NEW.application_number, ' status changed from ', OLD.application_status, ' to ', NEW.application_status)
        );
    END IF;
END //

DELIMITER ;


-- ============================================================================
-- Trigger: trg_disbursement_after_insert
-- Purpose: Log new disbursement records
-- ============================================================================

DELIMITER //

CREATE TRIGGER trg_disbursement_after_insert
AFTER INSERT ON disbursement
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        action_type, table_affected, record_id, new_values, action_description
    ) VALUES (
        'INSERT', 'disbursement', NEW.disbursement_id,
        JSON_OBJECT(
            'disbursement_id', NEW.disbursement_id,
            'application_id', NEW.application_id,
            'beneficiary_id', NEW.beneficiary_id,
            'disbursement_amount', NEW.disbursement_amount,
            'disbursement_status', NEW.disbursement_status
        ),
        CONCAT('Disbursement record created: ', NEW.disbursement_number, ' for amount ', NEW.disbursement_amount)
    );
END //

DELIMITER ;


-- ============================================================================
-- Trigger: trg_disbursement_after_update
-- Purpose: Log disbursement status changes
-- ============================================================================

DELIMITER //

CREATE TRIGGER trg_disbursement_after_update
AFTER UPDATE ON disbursement
FOR EACH ROW
BEGIN
    IF OLD.disbursement_status != NEW.disbursement_status THEN
        INSERT INTO audit_log (
            action_type, table_affected, record_id, old_values, new_values, action_description
        ) VALUES (
            'UPDATE', 'disbursement', NEW.disbursement_id,
            JSON_OBJECT('disbursement_status', OLD.disbursement_status),
            JSON_OBJECT('disbursement_status', NEW.disbursement_status),
            CONCAT('Disbursement ', NEW.disbursement_number, ' status changed from ', OLD.disbursement_status, ' to ', NEW.disbursement_status)
        );
    END IF;
END //

DELIMITER ;


-- ============================================================================
-- SECTION 7: VIEWS FOR COMMON QUERIES
-- ============================================================================
-- Views provide simplified access to frequently queried data combinations.


-- ============================================================================
-- View: vw_beneficiary_details
-- Purpose: Provides complete beneficiary information with location hierarchy
-- ============================================================================

CREATE OR REPLACE VIEW vw_beneficiary_details AS
SELECT 
    b.beneficiary_id,
    CONCAT(b.first_name, ' ', b.last_name) AS full_name,
    b.aadhaar_number,
    b.gender,
    b.date_of_birth,
    TIMESTAMPDIFF(YEAR, b.date_of_birth, CURDATE()) AS age,
    b.category,
    b.annual_income,
    b.disability_status,
    b.is_bpl,
    v.village_name,
    blk.block_name,
    d.district_name,
    s.state_name,
    b.is_active,
    b.created_at
FROM beneficiaries b
INNER JOIN villages v ON b.village_id = v.village_id
INNER JOIN blocks blk ON v.block_id = blk.block_id
INNER JOIN districts d ON blk.district_id = d.district_id
INNER JOIN states s ON d.state_id = s.state_id;


-- ============================================================================
-- View: vw_application_summary
-- Purpose: Provides application details with beneficiary and scheme info
-- ============================================================================

CREATE OR REPLACE VIEW vw_application_summary AS
SELECT 
    a.application_id,
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.aadhaar_number,
    sc.scheme_name,
    sc.scheme_category,
    a.application_date,
    a.application_status,
    a.applied_amount,
    a.approved_amount,
    u.username AS reviewed_by_username
FROM applications a
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes sc ON a.scheme_id = sc.scheme_id
LEFT JOIN users u ON a.reviewed_by = u.user_id;


-- ============================================================================
-- View: vw_eligibility_report
-- Purpose: Provides eligibility scores with application details
-- ============================================================================

CREATE OR REPLACE VIEW vw_eligibility_report AS
SELECT 
    e.eligibility_id,
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    sc.scheme_name,
    e.age_score,
    e.income_score,
    e.category_score,
    e.disability_score,
    e.bpl_score,
    e.location_score,
    e.total_eligibility_score,
    e.max_possible_score,
    ROUND((e.total_eligibility_score / e.max_possible_score) * 100, 2) AS eligibility_percentage,
    e.eligibility_status,
    e.evaluation_date
FROM eligibility e
INNER JOIN applications a ON e.application_id = a.application_id
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes sc ON a.scheme_id = sc.scheme_id;


-- ============================================================================
-- View: vw_disbursement_tracker
-- Purpose: Provides disbursement details with beneficiary information
-- ============================================================================

CREATE OR REPLACE VIEW vw_disbursement_tracker AS
SELECT 
    d.disbursement_id,
    d.disbursement_number,
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.bank_account_number AS beneficiary_bank_account,
    d.disbursement_amount,
    d.disbursement_mode,
    d.disbursement_status,
    d.disbursement_date,
    d.transaction_reference,
    d.installment_number,
    d.total_installments,
    u.username AS authorized_by_username
FROM disbursement d
INNER JOIN applications a ON d.application_id = a.application_id
INNER JOIN beneficiaries b ON d.beneficiary_id = b.beneficiary_id
LEFT JOIN users u ON d.authorized_by = u.user_id;


-- ============================================================================
-- SECTION 8: SCHEMA CREATION COMPLETE
-- ============================================================================

SELECT 'Database schema created successfully!' AS status;
SELECT COUNT(*) AS total_tables_created 
FROM information_schema.tables 
WHERE table_schema = 'gov_beneficiary_db' 
AND table_type = 'BASE TABLE';
