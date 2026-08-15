-- ============================================================================
-- Government Subsidy/Grant Disbursement Tracking System
-- Schema regenerated to match the actual JPA entities used by the running
-- Spring Boot app (see application.properties -> spring.jpa.hibernate.ddl-auto).
-- Table/column names below are exactly what Hibernate creates from the
-- entity classes in src/main/java/.../entity/, so this file is safe to use
-- as the canonical reference schema, or to load manually with ddl-auto=validate.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS government_subsidy_db;
USE government_subsidy_db;

-- ----------------------------------------------------------------------------
-- Table: beneficiary   (entity: Beneficiary)
-- ----------------------------------------------------------------------------
CREATE TABLE beneficiary (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    beneficiary_uid      VARCHAR(50)     NOT NULL,
    first_name           VARCHAR(100)    NOT NULL,
    last_name            VARCHAR(100)    NULL,
    date_of_birth        DATE            NOT NULL,
    gender               VARCHAR(20)     NOT NULL,
    mobile_number        VARCHAR(15)     NOT NULL,
    email                VARCHAR(100)    NOT NULL,
    address_line1        VARCHAR(255)    NULL,
    address_line2        VARCHAR(255)    NULL,
    village_id           BIGINT          NULL,
    block_id             BIGINT          NULL,
    district_id          BIGINT          NULL,
    state_id             BIGINT          NULL,
    pincode              VARCHAR(10)     NOT NULL,
    bank_account_number  VARCHAR(50)     NULL,
    ifsc_code            VARCHAR(20)     NULL,
    bank_name            VARCHAR(100)    NULL,
    category             VARCHAR(20)     NULL,
    annual_income        DECIMAL(12,2)   NULL,
    disability_status    BOOLEAN         NOT NULL DEFAULT FALSE,
    aadhaar_verified     BOOLEAN         NOT NULL DEFAULT FALSE,
    bank_verified        BOOLEAN         NOT NULL DEFAULT FALSE,
    is_active            BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uq_beneficiary_uid UNIQUE (beneficiary_uid),
    CONSTRAINT chk_beneficiary_category CHECK (category IS NULL OR category IN ('General','SC','ST','OBC','EWS'))
) ENGINE=InnoDB COMMENT='Beneficiary personal, contact, banking and eligibility profile';


-- ----------------------------------------------------------------------------
-- Table: scheme   (entity: Scheme)
-- ----------------------------------------------------------------------------
CREATE TABLE scheme (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    scheme_code          VARCHAR(20)     NOT NULL,
    name                 VARCHAR(200)    NOT NULL,
    description          TEXT            NULL,
    scheme_type          VARCHAR(50)     NULL,
    disbursement_mode    VARCHAR(50)     NULL,
    frequency            VARCHAR(50)     NULL,
    amount               DECIMAL(12,2)   NOT NULL,
    max_annual_income    DECIMAL(12,2)   NULL,
    allowed_categories   VARCHAR(100)    NULL,
    max_beneficiaries    INT             NULL,
    start_date           DATE            NOT NULL,
    end_date             DATE            NULL,
    is_active            BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uq_scheme_code UNIQUE (scheme_code)
) ENGINE=InnoDB COMMENT='Scheme master data and eligibility/grant configuration';


-- ----------------------------------------------------------------------------
-- Table: applications   (entity: Application)
-- ----------------------------------------------------------------------------
CREATE TABLE applications (
    application_id       BIGINT          NOT NULL AUTO_INCREMENT,
    assigned_officer     VARCHAR(255)    NULL,
    beneficiary_id       BIGINT          NOT NULL,
    scheme_id            BIGINT          NOT NULL,
    application_date     DATE            NULL,
    status               VARCHAR(50)     NULL,
    eligibility_score    DOUBLE          NULL,
    remarks              TEXT            NULL,
    applied_amount       DECIMAL(12,2)   NULL,
    approved_amount      DECIMAL(12,2)   NULL,

    PRIMARY KEY (application_id),
    CONSTRAINT fk_applications_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES beneficiary(id),
    CONSTRAINT fk_applications_scheme FOREIGN KEY (scheme_id) REFERENCES scheme(id)
) ENGINE=InnoDB COMMENT='Grant applications submitted by beneficiaries';


-- ----------------------------------------------------------------------------
-- Table: verifications   (entity: Verification)
-- One row per verification STAGE (Field Officer, District Officer, etc.) -
-- an application can have several, forming its audit trail.
-- ----------------------------------------------------------------------------
CREATE TABLE verifications (
    verification_id      BIGINT          NOT NULL AUTO_INCREMENT,
    application_id       BIGINT          NOT NULL,
    verification_status  VARCHAR(30)     NOT NULL,
    verified_by          VARCHAR(100)    NOT NULL,
    verification_date    DATE            NOT NULL,
    remarks               VARCHAR(500)   NULL,

    PRIMARY KEY (verification_id),
    CONSTRAINT fk_verifications_application FOREIGN KEY (application_id) REFERENCES applications(application_id)
) ENGINE=InnoDB COMMENT='Multi-level verification stage history per application';


-- ----------------------------------------------------------------------------
-- Table: finance_approvals   (entity: FinanceApproval)
-- One-to-one with applications.
-- ----------------------------------------------------------------------------
CREATE TABLE finance_approvals (
    approval_id          BIGINT          NOT NULL AUTO_INCREMENT,
    application_id       BIGINT          NOT NULL,
    approval_status      VARCHAR(30)     NOT NULL,
    approved_by          VARCHAR(100)    NOT NULL,
    approval_date        DATE            NOT NULL,
    remarks               VARCHAR(500)   NULL,

    PRIMARY KEY (approval_id),
    CONSTRAINT uq_finance_approvals_application UNIQUE (application_id),
    CONSTRAINT fk_finance_approvals_application FOREIGN KEY (application_id) REFERENCES applications(application_id)
) ENGINE=InnoDB COMMENT='Finance officer approval decision per application';


-- ----------------------------------------------------------------------------
-- Table: compliance_milestones   (entity: ComplianceMilestone)
-- ----------------------------------------------------------------------------
CREATE TABLE compliance_milestones (
    milestone_id         BIGINT          NOT NULL AUTO_INCREMENT,
    application_id       BIGINT          NOT NULL,
    milestone_type        VARCHAR(50)    NULL,
    due_date              DATE           NULL,
    status                VARCHAR(30)    NULL,
    completed_date        DATE           NULL,
    remarks               VARCHAR(500)   NULL,

    PRIMARY KEY (milestone_id),
    CONSTRAINT fk_milestones_application FOREIGN KEY (application_id) REFERENCES applications(application_id)
) ENGINE=InnoDB COMMENT='Compliance milestones tied to staged disbursement installments';


-- ----------------------------------------------------------------------------
-- Table: disbursement_plans   (entity: DisbursementPlan)
-- ----------------------------------------------------------------------------
CREATE TABLE disbursement_plans (
    plan_id               BIGINT         NOT NULL AUTO_INCREMENT,
    application_id        BIGINT         NOT NULL,
    total_grant_amount     DOUBLE        NULL,
    number_of_installments INT           NULL,
    created_date           DATE          NULL,
    status                 VARCHAR(30)   NULL,

    PRIMARY KEY (plan_id),
    CONSTRAINT fk_plans_application FOREIGN KEY (application_id) REFERENCES applications(application_id)
) ENGINE=InnoDB COMMENT='Staged disbursement plan per approved application';


-- ----------------------------------------------------------------------------
-- Table: disbursement_installments   (entity: DisbursementInstallment)
-- ----------------------------------------------------------------------------
CREATE TABLE disbursement_installments (
    installment_id        BIGINT         NOT NULL AUTO_INCREMENT,
    plan_id                BIGINT        NOT NULL,
    milestone_id           BIGINT        NULL,
    installment_number     INT           NULL,
    installment_amount     DOUBLE        NULL,
    scheduled_date         DATE          NULL,
    actual_release_date    DATE          NULL,
    status                 VARCHAR(30)   NULL,

    PRIMARY KEY (installment_id),
    CONSTRAINT fk_installments_plan FOREIGN KEY (plan_id) REFERENCES disbursement_plans(plan_id),
    CONSTRAINT fk_installments_milestone FOREIGN KEY (milestone_id) REFERENCES compliance_milestones(milestone_id)
) ENGINE=InnoDB COMMENT='Individual scheduled/released installments within a disbursement plan';


-- ----------------------------------------------------------------------------
-- Table: users   (entity: User)
-- Was missing from this file even though sample_data.sql inserts into it —
-- only worked before because ddl-auto=update let Hibernate create it
-- silently. Added so this file is actually usable standalone (ddl-auto=validate
-- or a fresh manual deploy).
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id              BIGINT          NOT NULL AUTO_INCREMENT,
    username             VARCHAR(50)     NOT NULL,
    password_hash        VARCHAR(255)    NOT NULL,
    full_name            VARCHAR(150)    NOT NULL,
    role                 VARCHAR(30)     NOT NULL,
    is_active            BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT chk_users_role CHECK (role IN ('FIELD_OFFICER','DISTRICT_OFFICER','FINANCE_APPROVER','ADMIN'))
) ENGINE=InnoDB COMMENT='System user accounts, one row per staff login';


-- ----------------------------------------------------------------------------
-- Table: audit_log   (entity: AuditLog)
-- ----------------------------------------------------------------------------
CREATE TABLE audit_log (
    log_id               BIGINT          NOT NULL AUTO_INCREMENT,
    action_type          VARCHAR(100)    NULL,
    performed_by         VARCHAR(100)    NULL,
    entity_affected      VARCHAR(100)    NULL,
    details              TEXT            NULL,
    timestamp            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (log_id)
) ENGINE=InnoDB COMMENT='Compliance audit trail — one row per logged action';