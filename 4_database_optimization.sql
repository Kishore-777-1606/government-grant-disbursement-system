-- ============================================================================
-- GOVERNMENT BENEFICIARY DISBURSEMENT SYSTEM
-- Database Optimization
--
-- Description: This script implements comprehensive database optimization
--              including indexes, query optimization, partitioning strategies,
--              and performance tuning for the Government Beneficiary
--              Disbursement System.
--
-- Compatible With: MySQL 8.0+
-- Author: Database & Eligibility Scoring Module
-- Version: 1.0
-- ============================================================================

USE gov_beneficiary_db;


-- ============================================================================
-- SECTION 1: PRIMARY INDEXES
-- ============================================================================
-- Primary key indexes are already created with tables.
-- Additional composite and covering indexes are added below.


-- ============================================================================
-- SECTION 2: INDEXES FOR REFERENCE TABLES
-- ============================================================================
-- Optimizes lookups on geographic and role data.


-- States table indexes
-- Purpose: Fast lookups by state code and name for filtering
CREATE INDEX idx_states_code ON states(state_code);
CREATE INDEX idx_states_name ON states(state_name);
CREATE INDEX idx_states_active ON states(is_active);


-- Districts table indexes
-- Purpose: Fast lookups by state_id for JOIN operations and filtering
CREATE INDEX idx_districts_state_id ON districts(state_id);
CREATE INDEX idx_districts_code ON districts(district_code);
CREATE INDEX idx_districts_name ON districts(district_name);
CREATE INDEX idx_districts_state_name ON districts(state_id, district_name);


-- Blocks table indexes
-- Purpose: Fast lookups by district_id for hierarchical queries
CREATE INDEX idx_blocks_district_id ON blocks(district_id);
CREATE INDEX idx_blocks_code ON blocks(block_code);
CREATE INDEX idx_blocks_name ON blocks(block_name);
CREATE INDEX idx_blocks_district_name ON blocks(district_id, block_name);


-- Villages table indexes
-- Purpose: Fast lookups by block_id and population-based queries
CREATE INDEX idx_villages_block_id ON villages(block_id);
CREATE INDEX idx_villages_code ON villages(village_code);
CREATE INDEX idx_villages_name ON villages(village_name);
CREATE INDEX idx_villages_pin_code ON villages(pin_code);
CREATE INDEX idx_villages_population ON villages(population);
CREATE INDEX idx_villages_block_name ON villages(block_id, village_name);


-- Roles table indexes
-- Purpose: Fast lookups by role name for user assignment
CREATE INDEX idx_roles_name ON roles(role_name);
CREATE INDEX idx_roles_active ON roles(is_active);


-- ============================================================================
-- SECTION 3: INDEXES FOR USERS TABLE
-- ============================================================================
-- Optimizes authentication, role-based access, and user lookups.


-- Basic lookup indexes
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_users_village_id ON users(village_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_employee_code ON users(employee_code);
CREATE INDEX idx_users_active ON users(is_active);

-- Composite index for authentication (username + active status)
CREATE INDEX idx_users_auth ON users(username, is_active, password_hash(64));

-- Composite index for role-based queries
CREATE INDEX idx_users_role_active ON users(role_id, is_active);

-- Index for last login tracking
CREATE INDEX idx_users_last_login ON users(last_login_at);

-- Composite index for user search (name + role)
CREATE INDEX idx_users_name_role ON users(last_name, first_name, role_id);


-- ============================================================================
-- SECTION 4: INDEXES FOR BENEFICIARIES TABLE
-- ============================================================================
-- Optimizes beneficiary search, demographic filtering, and deduplication checks.


-- Basic lookup indexes
CREATE INDEX idx_beneficiaries_village_id ON beneficiaries(village_id);
CREATE INDEX idx_beneficiaries_aadhaar ON beneficiaries(aadhaar_number);
CREATE INDEX idx_beneficiaries_pan ON beneficiaries(pan_number);
CREATE INDEX idx_beneficiaries_phone ON beneficiaries(phone_number);
CREATE INDEX idx_beneficiaries_email ON beneficiaries(email);
CREATE INDEX idx_beneficiaries_active ON beneficiaries(is_active);

-- Demographic filtering indexes
CREATE INDEX idx_beneficiaries_gender ON beneficiaries(gender);
CREATE INDEX idx_beneficiaries_category ON beneficiaries(category);
CREATE INDEX idx_beneficiaries_dob ON beneficiaries(date_of_birth);
CREATE INDEX idx_beneficiaries_income ON beneficiaries(annual_income);
CREATE INDEX idx_beneficiaries_bpl ON beneficiaries(is_bpl);
CREATE INDEX idx_beneficiaries_disability ON beneficiaries(disability_status);

-- Composite index for eligibility-based queries (income + category + BPL)
CREATE INDEX idx_beneficiaries_eligibility ON beneficiaries(
    annual_income, category, is_bpl, disability_status
);

-- Composite index for demographic search (name + village)
CREATE INDEX idx_beneficiaries_name_village ON beneficiaries(
    last_name, first_name, village_id
);

-- Composite index for age-based queries
CREATE INDEX idx_beneficiaries_dob_gender ON beneficiaries(date_of_birth, gender);

-- Index for active beneficiaries with BPL status
CREATE INDEX idx_beneficiaries_bpl_active ON beneficiaries(is_bpl, is_active);

-- Covering index for common beneficiary listing query
CREATE INDEX idx_beneficiaries_listing ON beneficiaries(
    beneficiary_id, first_name, last_name, village_id,
    category, annual_income, is_bpl, is_active
);


-- ============================================================================
-- SECTION 5: INDEXES FOR SCHEMES TABLE
-- ============================================================================
-- Optimizes scheme lookups, eligibility criteria filtering, and category queries.


-- Basic lookup indexes
CREATE INDEX idx_schemes_code ON schemes(scheme_code);
CREATE INDEX idx_schemes_name ON schemes(scheme_name);
CREATE INDEX idx_schemes_category ON schemes(scheme_category);
CREATE INDEX idx_schemes_active ON schemes(is_active);

-- Eligibility criteria indexes
CREATE INDEX idx_schemes_age_range ON schemes(min_age, max_age);
CREATE INDEX idx_schemes_income_range ON schemes(min_annual_income, max_annual_income);
CREATE INDEX idx_schemes_bpl ON schemes(requires_bpl);
CREATE INDEX idx_schemes_disability ON schemes(requires_disability);

-- Effective date range index for scheme validity queries
CREATE INDEX idx_schemes_effective ON schemes(effective_from, effective_to);

-- Composite index for active scheme search by category
CREATE INDEX idx_schemes_category_active ON schemes(scheme_category, is_active);

-- Composite index for eligibility matching
CREATE INDEX idx_schemes_eligibility ON schemes(
    min_age, max_age, max_annual_income,
    requires_bpl, requires_disability, is_active
);


-- ============================================================================
-- SECTION 6: INDEXES FOR APPLICATIONS TABLE
-- ============================================================================
-- Optimizes application status queries, beneficiary tracking, and reporting.


-- Basic lookup indexes
CREATE INDEX idx_applications_beneficiary_id ON applications(beneficiary_id);
CREATE INDEX idx_applications_scheme_id ON applications(scheme_id);
CREATE INDEX idx_applications_status ON applications(application_status);
CREATE INDEX idx_applications_date ON applications(application_date);
CREATE INDEX idx_applications_number ON applications(application_number);
CREATE INDEX idx_applications_reviewed_by ON applications(reviewed_by);

-- Composite index for status-based workflow queries
CREATE INDEX idx_applications_status_date ON applications(application_status, application_date);

-- Composite index for beneficiary scheme tracking
CREATE INDEX idx_applications_beneficiary_scheme ON applications(beneficiary_id, scheme_id);

-- Composite index for scheme status reporting
CREATE INDEX idx_applications_scheme_status ON applications(scheme_id, application_status);

-- Composite index for reviewer workload tracking
CREATE INDEX idx_applications_reviewer_status ON applications(reviewed_by, application_status);

-- Index for amount-based queries
CREATE INDEX idx_applications_applied_amount ON applications(applied_amount);
CREATE INDEX idx_applications_approved_amount ON applications(approved_amount);

-- Covering index for application listing report
CREATE INDEX idx_applications_listing ON applications(
    application_id, beneficiary_id, scheme_id,
    application_number, application_status, application_date,
    applied_amount, approved_amount
);

-- Composite index for date range queries
CREATE INDEX idx_applications_date_status ON applications(application_date, application_status);


-- ============================================================================
-- SECTION 7: INDEXES FOR ELIGIBILITY TABLE
-- ============================================================================
-- Optimizes eligibility scoring queries and report generation.


-- Basic lookup indexes
CREATE INDEX idx_eligibility_application_id ON eligibility(application_id);
CREATE INDEX idx_eligibility_status ON eligibility(eligibility_status);
CREATE INDEX idx_eligibility_evaluated_by ON eligibility(evaluated_by);
CREATE INDEX idx_eligibility_date ON eligibility(evaluation_date);

-- Score-based indexes for ranking and reporting
CREATE INDEX idx_eligibility_total_score ON eligibility(total_eligibility_score);
CREATE INDEX idx_eligibility_status_score ON eligibility(eligibility_status, total_eligibility_score);

-- Composite index for scoring breakdown analysis
CREATE INDEX idx_eligibility_scores ON eligibility(
    age_score, income_score, category_score,
    disability_score, bpl_score, location_score
);

-- Composite index for evaluation reporting
CREATE INDEX idx_eligibility_report ON eligibility(
    eligibility_id, application_id, total_eligibility_score,
    eligibility_status, evaluation_date
);

-- Index for percentage-based queries
CREATE INDEX idx_eligibility_percentage ON eligibility(
    total_eligibility_score, max_possible_score
);

-- Composite index for evaluator workload
CREATE INDEX idx_eligibility_evaluator_date ON eligibility(evaluated_by, evaluation_date);


-- ============================================================================
-- SECTION 8: INDEXES FOR VERIFICATION TABLE
-- ============================================================================
-- Optimizes verification status tracking and workflow management.


-- Basic lookup indexes
CREATE INDEX idx_verification_application_id ON verification(application_id);
CREATE INDEX idx_verification_verifier_id ON verification(verifier_id);
CREATE INDEX idx_verification_type ON verification(verification_type);
CREATE INDEX idx_verification_status ON verification(verification_status);
CREATE INDEX idx_verification_date ON verification(verification_date);

-- Composite index for verification workflow
CREATE INDEX idx_verification_status_type ON verification(verification_status, verification_type);

-- Composite index for verifier workload tracking
CREATE INDEX idx_verification_verifier_status ON verification(verifier_id, verification_status);

-- Composite index for verification report
CREATE INDEX idx_verification_report ON verification(
    verification_id, application_id, verification_type,
    verification_status, verification_date
);

-- Index for verification completeness tracking
CREATE INDEX idx_verification_completeness ON verification(
    document_verified, field_visit_done, aadhaar_verified,
    income_verified, address_verified
);


-- ============================================================================
-- SECTION 9: INDEXES FOR DISBURSEMENT TABLE
-- ============================================================================
-- Optimizes payment tracking, transaction queries, and financial reporting.


-- Basic lookup indexes
CREATE INDEX idx_disbursement_application_id ON disbursement(application_id);
CREATE INDEX idx_disbursement_beneficiary_id ON disbursement(beneficiary_id);
CREATE INDEX idx_disbursement_number ON disbursement(disbursement_number);
CREATE INDEX idx_disbursement_status ON disbursement(disbursement_status);
CREATE INDEX idx_disbursement_date ON disbursement(disbursement_date);
CREATE INDEX idx_disbursement_mode ON disbursement(disbursement_mode);
CREATE INDEX idx_disbursement_authorized_by ON disbursement(authorized_by);

-- Amount-based indexes for financial queries
CREATE INDEX idx_disbursement_amount ON disbursement(disbursement_amount);

-- Composite index for payment status tracking
CREATE INDEX idx_disbursement_status_date ON disbursement(disbursement_status, disbursement_date);

-- Composite index for beneficiary payment history
CREATE INDEX idx_disbursement_beneficiary_date ON disbursement(beneficiary_id, disbursement_date);

-- Composite index for installment tracking
CREATE INDEX idx_disbursement_installment ON disbursement(
    application_id, installment_number, total_installments
);

-- Composite index for financial reporting
CREATE INDEX idx_disbursement_financial ON disbursement(
    disbursement_date, disbursement_amount, disbursement_status
);

-- Composite index for authorized disbursement tracking
CREATE INDEX idx_disbursement_authorization ON disbursement(
    authorized_by, disbursement_status, authorized_at
);

-- Covering index for disbursement listing report
CREATE INDEX idx_disbursement_listing ON disbursement(
    disbursement_id, application_id, beneficiary_id,
    disbursement_number, disbursement_amount, disbursement_status,
    disbursement_date
);

-- Index for transaction reference lookups
CREATE INDEX idx_disbursement_transaction ON disbursement(transaction_reference);


-- ============================================================================
-- SECTION 10: INDEXES FOR AUDIT LOG TABLE
-- ============================================================================
-- Optimizes audit trail queries, compliance reporting, and activity analysis.


-- Basic lookup indexes
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_action_type ON audit_log(action_type);
CREATE INDEX idx_audit_log_table_affected ON audit_log(table_affected);
CREATE INDEX idx_audit_log_record_id ON audit_log(record_id);
CREATE INDEX idx_audit_log_timestamp ON audit_log(action_timestamp);

-- Composite index for user activity tracking
CREATE INDEX idx_audit_log_user_timestamp ON audit_log(user_id, action_timestamp);

-- Composite index for table activity analysis
CREATE INDEX idx_audit_log_table_action ON audit_log(table_affected, action_type);

-- Composite index for compliance reporting (date + user + action)
CREATE INDEX idx_audit_log_compliance ON audit_log(
    action_timestamp, user_id, action_type, table_affected
);

-- Composite index for action type analysis
CREATE INDEX idx_audit_log_action_timestamp ON audit_log(action_type, action_timestamp);

-- Index for IP-based security analysis
CREATE INDEX idx_audit_log_ip ON audit_log(ip_address);


-- ============================================================================
-- SECTION 11: INDEXES FOR ELIGIBILITY SCORING CONFIG TABLE
-- ============================================================================

CREATE INDEX idx_scoring_config_name ON eligibility_scoring_config(config_name);
CREATE INDEX idx_scoring_config_active ON eligibility_scoring_config(is_active);


-- ============================================================================
-- SECTION 12: PARTITIONING FOR LARGE TABLES
-- ============================================================================
-- Partitioning strategies for tables expected to grow large over time.


-- ============================================================================
-- Partitioning: audit_log by range on action_timestamp
-- Purpose: Efficient data archival and compliance queries by date range
-- ============================================================================

-- Note: Partitioning must be applied when table is created or rebuilt
-- For existing tables, we document the partitioning strategy here.
-- In production, the audit_log table would be partitioned as follows:

/*
-- Example partitioning for audit_log (apply during table recreation):
ALTER TABLE audit_log PARTITION BY RANGE (YEAR(action_timestamp)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p2027 VALUES LESS THAN (2028),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);
*/


-- ============================================================================
-- SECTION 13: TABLE OPTIMIZATION COMMANDS
-- ============================================================================
-- Commands to optimize table storage and performance.


-- Analyze tables to update index statistics
ANALYZE TABLE roles;
ANALYZE TABLE states;
ANALYZE TABLE districts;
ANALYZE TABLE blocks;
ANALYZE TABLE villages;
ANALYZE TABLE users;
ANALYZE TABLE beneficiaries;
ANALYZE TABLE schemes;
ANALYZE TABLE applications;
ANALYZE TABLE eligibility;
ANALYZE TABLE verification;
ANALYZE TABLE disbursement;
ANALYZE TABLE audit_log;
ANALYZE TABLE eligibility_scoring_config;


-- Optimize tables to defragment and reclaim space
OPTIMIZE TABLE roles;
OPTIMIZE TABLE states;
OPTIMIZE TABLE districts;
OPTIMIZE TABLE blocks;
OPTIMIZE TABLE villages;
OPTIMIZE TABLE users;
OPTIMIZE TABLE beneficiaries;
OPTIMIZE TABLE schemes;
OPTIMIZE TABLE applications;
OPTIMIZE TABLE eligibility;
OPTIMIZE TABLE verification;
OPTIMIZE TABLE disbursement;
OPTIMIZE TABLE audit_log;
OPTIMIZE TABLE eligibility_scoring_config;


-- ============================================================================
-- SECTION 14: QUERY OPTIMIZATION VIEWS
-- ============================================================================
-- Pre-built views that optimize commonly executed complex queries.


-- ============================================================================
-- View: vw_location_hierarchy
-- Purpose: Pre-joined geographic hierarchy for fast location lookups
-- ============================================================================

DROP VIEW IF EXISTS vw_location_hierarchy;

CREATE OR REPLACE VIEW vw_location_hierarchy AS
SELECT 
    v.village_id,
    v.village_name,
    v.village_code,
    v.pin_code AS village_pin_code,
    v.population,
    b.block_id,
    b.block_name,
    b.block_code,
    d.district_id,
    d.district_name,
    d.district_code,
    s.state_id,
    s.state_name,
    s.state_code
FROM villages v
INNER JOIN blocks b ON v.block_id = b.block_id
INNER JOIN districts d ON b.district_id = d.district_id
INNER JOIN states s ON d.state_id = s.state_id
WHERE v.is_active = TRUE AND b.is_active = TRUE 
AND d.is_active = TRUE AND s.is_active = TRUE;


-- ============================================================================
-- View: vw_active_schemes_with_criteria
-- Purpose: Pre-processed scheme eligibility criteria for fast matching
-- ============================================================================

DROP VIEW IF EXISTS vw_active_schemes_with_criteria;

CREATE OR REPLACE VIEW vw_active_schemes_with_criteria AS
SELECT 
    scheme_id,
    scheme_code,
    scheme_name,
    scheme_category,
    min_age,
    max_age,
    min_annual_income,
    max_annual_income,
    eligible_categories,
    requires_disability,
    requires_bpl,
    max_disbursement_amount,
    installment_count,
    effective_from,
    effective_to,
    CASE 
        WHEN effective_to IS NULL THEN TRUE
        WHEN effective_to >= CURDATE() THEN TRUE
        ELSE FALSE
    END AS currently_valid
FROM schemes
WHERE is_active = TRUE;


-- ============================================================================
-- View: vw_application_workflow_status
-- Purpose: Pre-joined application workflow data for dashboard display
-- ============================================================================

DROP VIEW IF EXISTS vw_application_workflow_status;

CREATE OR REPLACE VIEW vw_application_workflow_status AS
SELECT 
    a.application_id,
    a.application_number,
    a.application_status,
    a.application_date,
    a.applied_amount,
    a.approved_amount,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.aadhaar_number,
    s.scheme_name,
    s.scheme_category,
    e.total_eligibility_score,
    e.eligibility_status,
    v.verification_status,
    v.document_verified,
    v.field_visit_done,
    v.aadhaar_verified,
    d.disbursement_status,
    d.disbursement_amount,
    d.disbursement_date,
    DATEDIFF(CURDATE(), a.application_date) AS days_since_application
FROM applications a
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
LEFT JOIN eligibility e ON a.application_id = e.application_id
LEFT JOIN verification v ON a.application_id = v.application_id
LEFT JOIN disbursement d ON a.application_id = d.application_id;


-- ============================================================================
-- View: vw_disbursement_summary
-- Purpose: Pre-aggregated disbursement data for financial reporting
-- ============================================================================

DROP VIEW IF EXISTS vw_disbursement_summary;

CREATE OR REPLACE VIEW vw_disbursement_summary AS
SELECT 
    s.scheme_name,
    s.scheme_category,
    COUNT(DISTINCT a.application_id) AS total_applications,
    COUNT(DISTINCT CASE WHEN a.application_status = 'Approved' THEN a.application_id END) AS approved_count,
    COUNT(DISTINCT CASE WHEN d.disbursement_status = 'Completed' THEN d.disbursement_id END) AS disbursed_count,
    SUM(CASE WHEN d.disbursement_status = 'Completed' THEN d.disbursement_amount ELSE 0 END) AS total_disbursed,
    SUM(CASE WHEN d.disbursement_status = 'Processing' THEN d.disbursement_amount ELSE 0 END) AS pending_amount,
    SUM(CASE WHEN d.disbursement_status = 'Failed' THEN d.disbursement_amount ELSE 0 END) AS failed_amount
FROM applications a
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
LEFT JOIN disbursement d ON a.application_id = d.application_id
GROUP BY s.scheme_id, s.scheme_name, s.scheme_category;


-- ============================================================================
-- SECTION 15: PERFORMANCE MONITORING QUERIES
-- ============================================================================
-- Queries to monitor database performance and index usage.


-- Query: Show index usage statistics
-- Purpose: Identifies which indexes are being used effectively
SELECT 
    database_name,
    table_name,
    index_name,
    rows_selected,
    rows_inserted,
    rows_updated,
    rows_deleted
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE database_name = 'gov_beneficiary_db'
AND index_name IS NOT NULL
ORDER BY rows_selected DESC;


-- Query: Show table statistics
-- Purpose: Monitors table sizes and row counts
SELECT 
    table_name,
    table_rows,
    ROUND(data_length / 1024 / 1024, 2) AS data_size_mb,
    ROUND(index_length / 1024 / 1024, 2) AS index_size_mb,
    ROUND((data_length + index_length) / 1024 / 1024, 2) AS total_size_mb
FROM information_schema.tables
WHERE table_schema = 'gov_beneficiary_db'
AND table_type = 'BASE TABLE'
ORDER BY (data_length + index_length) DESC;


-- Query: Show index statistics
-- Purpose: Reviews all indexes and their structure
SELECT 
    table_name,
    index_name,
    column_name,
    cardinality,
    non_unique,
    nullable
FROM information_schema.statistics
WHERE table_schema = 'gov_beneficiary_db'
ORDER BY table_name, index_name, seq_in_index;


-- Query: Show foreign key constraints
-- Purpose: Reviews all relationships for integrity verification
SELECT 
    constraint_name,
    table_name,
    column_name,
    referenced_table_name,
    referenced_column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'gov_beneficiary_db'
AND referenced_table_name IS NOT NULL
ORDER BY table_name, constraint_name;


-- ============================================================================
-- SECTION 16: QUERY OPTIMIZATION TECHNIQUES
-- ============================================================================
-- Demonstrates optimized query patterns for common operations.


-- Technique 1: Use covering indexes for common SELECT queries
-- Optimized: Uses idx_applications_listing covering index
EXPLAIN SELECT 
    application_id, beneficiary_id, scheme_id,
    application_number, application_status, application_date,
    applied_amount, approved_amount
FROM applications
WHERE application_status = 'Approved'
ORDER BY application_date DESC;


-- Technique 2: Use composite index for multi-column filtering
-- Optimized: Uses idx_beneficiaries_eligibility composite index
EXPLAIN SELECT 
    beneficiary_id, first_name, last_name,
    annual_income, category, is_bpl
FROM beneficiaries
WHERE annual_income < 50000
AND category IN ('SC', 'ST')
AND is_bpl = TRUE;


-- Technique 3: Use index for JOIN optimization
-- Optimized: Uses primary keys and foreign key indexes
EXPLAIN SELECT 
    a.application_number,
    b.first_name,
    s.scheme_name,
    e.total_eligibility_score
FROM applications a
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
LEFT JOIN eligibility e ON a.application_id = e.application_id
WHERE a.application_status = 'Approved';


-- Technique 4: Use index for aggregate queries
-- Optimized: Uses idx_applications_scheme_status composite index
EXPLAIN SELECT 
    scheme_id,
    application_status,
    COUNT(*) AS count,
    SUM(applied_amount) AS total_amount
FROM applications
GROUP BY scheme_id, application_status;


-- Technique 5: Use index for range queries
-- Optimized: Uses idx_beneficiaries_income for range scan
EXPLAIN SELECT 
    beneficiary_id, first_name, last_name, annual_income
FROM beneficiaries
WHERE annual_income BETWEEN 20000 AND 100000
ORDER BY annual_income;


-- ============================================================================
-- SECTION 17: DATABASE MAINTENANCE PROCEDURES
-- ============================================================================
-- Procedures for routine database maintenance tasks.


-- ============================================================================
-- Procedure: sp_maintain_audit_log
-- Purpose: Archives old audit log entries and maintains log table size
-- Parameters: p_retention_days (default 365 days)
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_maintain_audit_log;

DELIMITER //

CREATE PROCEDURE sp_maintain_audit_log(
    IN p_retention_days INT
)
BEGIN
    DECLARE v_deleted_count INT;
    
    SET p_retention_days = COALESCE(p_retention_days, 365);
    
    -- Delete audit logs older than retention period
    DELETE FROM audit_log 
    WHERE action_timestamp < DATE_SUB(CURDATE(), INTERVAL p_retention_days DAY);
    
    SET v_deleted_count = ROW_COUNT();
    
    -- Optimize the table after deletion
    OPTIMIZE TABLE audit_log;
    
    SELECT 
        v_deleted_count AS records_deleted,
        p_retention_days AS retention_days_used,
        (SELECT COUNT(*) FROM audit_log) AS remaining_records;
    
END //

DELIMITER ;


-- ============================================================================
-- Procedure: sp_check_database_health
-- Purpose: Performs comprehensive database health check
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_check_database_health;

DELIMITER //

CREATE PROCEDURE sp_check_database_health()
BEGIN
    -- Table row counts
    SELECT 'TABLE ROW COUNTS' AS section;
    SELECT 
        table_name,
        table_rows
    FROM information_schema.tables
    WHERE table_schema = 'gov_beneficiary_db'
    AND table_type = 'BASE TABLE'
    ORDER BY table_name;
    
    -- Index count per table
    SELECT 'INDEX COUNT PER TABLE' AS section;
    SELECT 
        table_name,
        COUNT(DISTINCT index_name) AS index_count
    FROM information_schema.statistics
    WHERE table_schema = 'gov_beneficiary_db'
    GROUP BY table_name
    ORDER BY table_name;
    
    -- Foreign key integrity check
    SELECT 'FOREIGN KEY COUNT PER TABLE' AS section;
    SELECT 
        table_name,
        COUNT(*) AS fk_count
    FROM information_schema.key_column_usage
    WHERE table_schema = 'gov_beneficiary_db'
    AND referenced_table_name IS NOT NULL
    GROUP BY table_name
    ORDER BY table_name;
    
    -- Table sizes
    SELECT 'TABLE SIZES' AS section;
    SELECT 
        table_name,
        ROUND((data_length + index_length) / 1024 / 1024, 2) AS total_size_mb
    FROM information_schema.tables
    WHERE table_schema = 'gov_beneficiary_db'
    AND table_type = 'BASE TABLE'
    ORDER BY (data_length + index_length) DESC;
    
END //

DELIMITER ;


-- ============================================================================
-- SECTION 18: OPTIMIZATION COMPLETE
-- ============================================================================

SELECT 'Database Optimization completed successfully!' AS status;

-- Summary of optimization objects created
SELECT 'Optimization Summary' AS section;

SELECT 'Indexes Created' AS object_type, 
    (SELECT COUNT(*) FROM information_schema.statistics 
     WHERE table_schema = 'gov_beneficiary_db' 
     AND index_name != 'PRIMARY') AS count
UNION ALL
SELECT 'Views Created',
    (SELECT COUNT(*) FROM information_schema.views 
     WHERE table_schema = 'gov_beneficiary_db')
UNION ALL
SELECT 'Procedures Created',
    (SELECT COUNT(*) FROM information_schema.routines 
     WHERE routine_schema = 'gov_beneficiary_db' 
     AND routine_type = 'PROCEDURE')
UNION ALL
SELECT 'Functions Created',
    (SELECT COUNT(*) FROM information_schema.routines 
     WHERE routine_schema = 'gov_beneficiary_db' 
     AND routine_type = 'FUNCTION');
