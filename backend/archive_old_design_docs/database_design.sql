-- ============================================================
-- ⚠ LEGACY / NOT USED BY THE RUNNING APPLICATION ⚠
-- Earliest design pass — table names (country, disbursement_batch,
-- verification_tracker, etc.) don't match the entities the code actually
-- uses. See backend/6_actual_application_schema.sql for the real schema.
-- Kept for historical reference only.
-- ============================================================

-- ============================================================
-- MILESTONE 1: Database Design
-- Project: Beneficiary & Scheme Disbursement System
-- Technology: Spring Boot + Hibernate + JPA
-- ============================================================

-- ============================================================
-- 1. REGIONAL HIERARCHY
-- ============================================================

CREATE DATABASE government_subsidy_db;
USE government_subsidy_db;

CREATE TABLE country (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    code            VARCHAR(10) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE state (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    country_id      BIGINT NOT NULL,
    code            VARCHAR(10) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (country_id) REFERENCES country(id)
);

CREATE TABLE district (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    state_id        BIGINT NOT NULL,
    code            VARCHAR(10) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (state_id) REFERENCES state(id)
);

CREATE TABLE block (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    district_id     BIGINT NOT NULL,
    code            VARCHAR(10) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (district_id) REFERENCES district(id)
);

CREATE TABLE village (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    block_id        BIGINT NOT NULL,
    code            VARCHAR(10) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (block_id) REFERENCES block(id)
);

-- ============================================================
-- 2. BENEFICIARY MANAGEMENT
-- ============================================================

CREATE TABLE beneficiary (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    beneficiary_uid     VARCHAR(50) NOT NULL UNIQUE,   -- External unique ID (e.g., Aadhaar)
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100),
    date_of_birth       DATE NOT NULL,
    gender              ENUM('MALE','FEMALE','OTHER'),
    mobile_number       VARCHAR(15),
    email               VARCHAR(100),
    address_line1       VARCHAR(255),
    address_line2       VARCHAR(255),
    village_id          BIGINT,
    block_id            BIGINT,
    district_id         BIGINT,
    state_id            BIGINT,
    pincode             VARCHAR(10),
    bank_account_number VARCHAR(50),
    ifsc_code           VARCHAR(20),
    bank_name           VARCHAR(100),
    aadhaar_verified    BOOLEAN DEFAULT FALSE,
    bank_verified       BOOLEAN DEFAULT FALSE,
    is_active           BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (village_id)  REFERENCES village(id),
    FOREIGN KEY (block_id)    REFERENCES block(id),
    FOREIGN KEY (district_id) REFERENCES district(id),
    FOREIGN KEY (state_id)    REFERENCES state(id)
);

-- ============================================================
-- 3. SCHEME MANAGEMENT
-- ============================================================

CREATE TABLE scheme (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    scheme_code         VARCHAR(20) NOT NULL UNIQUE,
    name                VARCHAR(200) NOT NULL,
    description         TEXT,
    scheme_type         ENUM('CASH_TRANSFER','SUBSIDY','PENSION','SCHOLARSHIP','GRANT'),
    disbursement_mode   ENUM('BANK_TRANSFER','CHEQUE','CASH','VOUCHER'),
    frequency           ENUM('ONE_TIME','MONTHLY','QUARTERLY','YEARLY'),
    amount              DECIMAL(12,2) NOT NULL,
    max_beneficiaries   INT,
    start_date          DATE NOT NULL,
    end_date            DATE,
    is_active           BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- 4. ELIGIBILITY CRITERIA & RULES
-- ============================================================

CREATE TABLE eligibility_criteria (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    scheme_id           BIGINT NOT NULL,
    criteria_name       VARCHAR(100) NOT NULL,
    criteria_type       ENUM('AGE','INCOME','GENDER','CASTE','REGION','BANK_VERIFIED','AADHAAR_VERIFIED','CUSTOM'),
    operator            ENUM('EQ','NEQ','GT','GTE','LT','LTE','IN','BETWEEN'),
    value               VARCHAR(255) NOT NULL,
    score_weight        DECIMAL(5,2) DEFAULT 1.00,  -- Weight for scoring
    is_mandatory        BOOLEAN DEFAULT FALSE,       -- Hard fail if not met
    display_order       INT DEFAULT 0,
    is_active           BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (scheme_id) REFERENCES scheme(id)
);

-- ============================================================
-- 5. ELIGIBILITY SCORES (Cached per beneficiary-scheme)
-- ============================================================

CREATE TABLE eligibility_score (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    beneficiary_id      BIGINT NOT NULL,
    scheme_id           BIGINT NOT NULL,
    total_score         DECIMAL(10,2) DEFAULT 0.00,
    max_possible_score  DECIMAL(10,2) DEFAULT 0.00,
    eligibility_status  ENUM('PENDING','ELIGIBLE','NOT_ELIGIBLE') DEFAULT 'PENDING',
    evaluated_at        TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (beneficiary_id) REFERENCES beneficiary(id),
    FOREIGN KEY (scheme_id) REFERENCES scheme(id),
    UNIQUE KEY uk_beneficiary_scheme (beneficiary_id, scheme_id)
);

-- ============================================================
-- 6. BENEFICIARY SCHEME ENROLLMENT
-- ============================================================

CREATE TABLE beneficiary_scheme (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    beneficiary_id      BIGINT NOT NULL,
    scheme_id           BIGINT NOT NULL,
    enrollment_date     DATE NOT NULL,
    status              ENUM('APPLIED','VERIFIED','APPROVED','REJECTED','ACTIVE','SUSPENDED','CLOSED') DEFAULT 'APPLIED',
    rejection_reason    TEXT,
    approved_by         VARCHAR(100),
    approved_date       DATE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (beneficiary_id) REFERENCES beneficiary(id),
    FOREIGN KEY (scheme_id) REFERENCES scheme(id),
    UNIQUE KEY uk_enrollment (beneficiary_id, scheme_id)
);

-- ============================================================
-- 7. VERIFICATION WORKFLOW
-- ============================================================

CREATE TABLE verification_step (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    step_code           VARCHAR(30) NOT NULL UNIQUE,
    step_name           VARCHAR(100) NOT NULL,
    required_document   VARCHAR(200),
    display_order       INT NOT NULL,
    is_active           BOOLEAN DEFAULT TRUE
);

CREATE TABLE verification_tracker (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    beneficiary_scheme_id BIGINT NOT NULL,
    step_id             BIGINT NOT NULL,
    status              ENUM('PENDING','IN_PROGRESS','PASSED','FAILED','SKIPPED') DEFAULT 'PENDING',
    verified_by         VARCHAR(100),
    verified_at         TIMESTAMP,
    remarks             TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (beneficiary_scheme_id) REFERENCES beneficiary_scheme(id),
    FOREIGN KEY (step_id) REFERENCES verification_step(id),
    UNIQUE KEY uk_tracker_step (beneficiary_scheme_id, step_id)
);

-- ============================================================
-- 8. DISBURSEMENT PROCESS
-- ============================================================

CREATE TABLE disbursement_batch (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_code          VARCHAR(30) NOT NULL UNIQUE,
    scheme_id           BIGINT NOT NULL,
    total_beneficiaries INT DEFAULT 0,
    total_amount        DECIMAL(14,2) DEFAULT 0.00,
    status              ENUM('DRAFT','APPROVED','PROCESSING','COMPLETED','FAILED') DEFAULT 'DRAFT',
    initiated_by        VARCHAR(100),
    approved_by         VARCHAR(100),
    processed_at        TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (scheme_id) REFERENCES scheme(id)
);

CREATE TABLE disbursement_transaction (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id            BIGINT NOT NULL,
    beneficiary_scheme_id BIGINT NOT NULL,
    transaction_ref     VARCHAR(50) UNIQUE,
    amount              DECIMAL(12,2) NOT NULL,
    status              ENUM('PENDING','PROCESSING','SUCCESS','FAILED','REVERSED') DEFAULT 'PENDING',
    failure_reason      TEXT,
    gateway_response    TEXT,
    disbursed_at        TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES disbursement_batch(id),
    FOREIGN KEY (beneficiary_scheme_id) REFERENCES beneficiary_scheme(id)
);

-- ============================================================
-- 9. AUDIT / LOGGING
-- ============================================================

CREATE TABLE audit_log (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    entity_type         VARCHAR(50) NOT NULL,
    entity_id           BIGINT NOT NULL,
    action              VARCHAR(50) NOT NULL,
    previous_value      JSON,
    new_value           JSON,
    performed_by        VARCHAR(100),
    performed_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- INDEXES (for performance)
-- ============================================================

CREATE INDEX idx_beneficiary_aadhaar ON beneficiary(beneficiary_uid);
CREATE INDEX idx_beneficiary_mobile ON beneficiary(mobile_number);
CREATE INDEX idx_beneficiary_district ON beneficiary(district_id);
CREATE INDEX idx_beneficiary_state ON beneficiary(state_id);
CREATE INDEX idx_scheme_code ON scheme(scheme_code);
CREATE INDEX idx_scheme_type ON scheme(scheme_type);
CREATE INDEX idx_eligibility_scheme ON eligibility_criteria(scheme_id);
CREATE INDEX idx_enrollment_status ON beneficiary_scheme(status);
CREATE INDEX idx_disbursement_batch_scheme ON disbursement_batch(scheme_id);
CREATE INDEX idx_disbursement_txn_batch ON disbursement_transaction(batch_id);
CREATE INDEX idx_disbursement_txn_status ON disbursement_transaction(status);
CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);