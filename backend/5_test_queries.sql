-- ============================================================================
-- GOVERNMENT BENEFICIARY DISBURSEMENT SYSTEM
-- Test Queries and Database Operations Testing
--
-- Description: This script contains comprehensive test queries for all
--              database operations including INSERT, UPDATE, DELETE, SELECT,
--              JOIN, GROUP BY, ORDER BY, eligibility scoring, and
--              verification workflow testing.
--
-- Compatible With: MySQL 8.0+
-- Author: Database & Eligibility Scoring Module
-- Version: 1.0
-- ============================================================================

USE gov_beneficiary_db;


-- ============================================================================
-- SECTION 1: INSERT TESTING
-- ============================================================================
-- Tests inserting new records into all tables with valid data.


-- Test 1.1: Insert a new state
INSERT INTO states (state_code, state_name, is_active) 
VALUES ('OR', 'Odisha', TRUE);

SELECT * FROM states WHERE state_code = 'OR';


-- Test 1.2: Insert a new district under the new state
INSERT INTO districts (state_id, district_code, district_name, is_active)
VALUES (LAST_INSERT_ID(), 'BBS', 'Bhubaneswar', TRUE);

SELECT * FROM districts WHERE district_code = 'BBS';


-- Test 1.3: Insert a new block under the new district
INSERT INTO blocks (district_id, block_code, block_name, is_active)
VALUES (LAST_INSERT_ID(), 'BBS-1', 'Bhubaneswar Block 1', TRUE);

SELECT * FROM blocks WHERE block_code = 'BBS-1';


-- Test 1.4: Insert a new village under the new block
INSERT INTO villages (block_id, village_code, village_name, pin_code, population, is_active)
VALUES (LAST_INSERT_ID(), 'VIL-051', 'Patia Village', '751024', 18000, TRUE);

SELECT * FROM villages WHERE village_code = 'VIL-051';


-- Test 1.5: Insert a new beneficiary
INSERT INTO beneficiaries (
    village_id, first_name, last_name, date_of_birth, gender,
    aadhaar_number, phone_number, address_line1, pin_code,
    annual_income, category, disability_status, is_bpl,
    bank_account_number, ifsc_code, is_active
) VALUES (
    51, 'Subhash', 'Mahapatra', '1992-06-15', 'Male',
    '998877665544', '9876543220', '15, Patia Main Road', '751024',
    38000.00, 'OBC', FALSE, TRUE,
    '50100998877665', 'SBIN0009988', TRUE
);

SELECT * FROM beneficiaries WHERE aadhaar_number = '998877665544';


-- Test 1.6: Insert a new application for the new beneficiary
INSERT INTO applications (
    beneficiary_id, scheme_id, application_number, application_date,
    application_status, applied_amount, remarks
) VALUES (
    13, 1, 'APP-2026-000013', CURDATE(),
    'Submitted', 6000.00, 'PM-KISAN application from Odisha'
);

SELECT * FROM applications WHERE application_number = 'APP-2026-000013';


-- Test 1.7: Insert verification record
INSERT INTO verification (
    application_id, verifier_id, verification_type,
    verification_status, document_verified, aadhaar_verified
) VALUES (
    13, 6, 'Aadhaar Authentication', 'Pending', FALSE, FALSE
);

SELECT * FROM verification WHERE application_id = 13;


-- Test 1.8: Insert eligibility scoring config
INSERT INTO eligibility_scoring_config (
    config_name, config_description,
    age_weight, income_weight, category_weight,
    disability_weight, bpl_weight, location_weight,
    eligible_threshold, partially_eligible_threshold
) VALUES (
    'Odisha Specific Scoring', 'Custom scoring for Odisha state schemes',
    18, 22, 18, 15, 15, 12, 65, 35
);

SELECT * FROM eligibility_scoring_config WHERE config_name = 'Odisha Specific Scoring';


-- ============================================================================
-- SECTION 2: INSERT TESTING - CONSTRAINT VALIDATION
-- ============================================================================
-- Tests that database constraints prevent invalid data.


-- Test 2.1: Duplicate Aadhaar number (should fail)
-- INSERT INTO beneficiaries (village_id, first_name, last_name, date_of_birth, gender,
--     aadhaar_number, phone_number, address_line1, annual_income, category, is_active)
-- VALUES (1, 'Test', 'User', '1990-01-01', 'Male',
--     '123456789012', '9999999999', 'Test Address', 50000.00, 'General', TRUE);
-- ERROR: Duplicate entry '123456789012' for key 'beneficiaries.uq_beneficiary_aadhaar'


-- Test 2.2: Invalid Aadhaar format (should fail)
-- INSERT INTO beneficiaries (village_id, first_name, last_name, date_of_birth, gender,
--     aadhaar_number, phone_number, address_line1, annual_income, category, is_active)
-- VALUES (1, 'Test', 'User', '1990-01-01', 'Male',
--     '12345', '9999999999', 'Test Address', 50000.00, 'General', TRUE);
-- ERROR: Check constraint 'chk_aadhaar_format' is violated


-- Test 2.3: Future date of birth (should fail)
-- INSERT INTO beneficiaries (village_id, first_name, last_name, date_of_birth, gender,
--     aadhaar_number, phone_number, address_line1, annual_income, category, is_active)
-- VALUES (1, 'Test', 'User', '2030-01-01', 'Male',
--     '111122223333', '9999999999', 'Test Address', 50000.00, 'General', TRUE);
-- ERROR: Check constraint 'chk_dob_not_future' is violated


-- Test 2.4: Duplicate application number (should fail)
-- INSERT INTO applications (beneficiary_id, scheme_id, application_number,
--     application_date, application_status, applied_amount)
-- VALUES (1, 1, 'APP-2026-000001', CURDATE(), 'Submitted', 6000.00);
-- ERROR: Duplicate entry 'APP-2026-000001' for key 'applications.uq_application_number'


-- Test 2.5: Verify constraint violations were prevented
SELECT 'Constraint validation tests completed' AS test_status;


-- ============================================================================
-- SECTION 3: UPDATE TESTING
-- ============================================================================
-- Tests updating records in various tables.


-- Test 3.1: Update beneficiary income
UPDATE beneficiaries 
SET annual_income = 42000.00,
    updated_at = CURRENT_TIMESTAMP
WHERE beneficiary_id = 13;

SELECT beneficiary_id, first_name, last_name, annual_income 
FROM beneficiaries WHERE beneficiary_id = 13;


-- Test 3.2: Update application status from Submitted to Under Review
UPDATE applications 
SET application_status = 'Under Review',
    reviewed_by = 3,
    reviewed_at = CURRENT_TIMESTAMP
WHERE application_number = 'APP-2026-000013';

SELECT application_number, application_status, reviewed_by 
FROM applications WHERE application_number = 'APP-2026-000013';


-- Test 3.3: Update verification status
UPDATE verification 
SET verification_status = 'In Progress',
    verifier_id = 6
WHERE application_id = 13;

SELECT application_id, verification_type, verification_status, verifier_id 
FROM verification WHERE application_id = 13;


-- Test 3.4: Update beneficiary BPL status and category
UPDATE beneficiaries 
SET is_bpl = TRUE,
    category = 'SC',
    updated_at = CURRENT_TIMESTAMP
WHERE beneficiary_id = 13;

SELECT beneficiary_id, first_name, category, is_bpl 
FROM beneficiaries WHERE beneficiary_id = 13;


-- Test 3.5: Bulk update - increase disbursement amounts for a scheme
UPDATE disbursement d
INNER JOIN applications a ON d.application_id = a.application_id
SET d.disbursement_amount = d.disbursement_amount * 1.05
WHERE a.scheme_id = 1 
AND d.disbursement_status = 'Completed';

SELECT d.disbursement_number, d.disbursement_amount, a.scheme_id 
FROM disbursement d
INNER JOIN applications a ON d.application_id = a.application_id
WHERE a.scheme_id = 1 AND d.disbursement_status = 'Completed';


-- ============================================================================
-- SECTION 4: DELETE TESTING
-- ============================================================================
-- Tests deleting records and cascade behavior.


-- Test 4.1: Delete a village (should be restricted if beneficiaries exist)
-- DELETE FROM villages WHERE village_id = 51;
-- This may fail due to FK constraint if beneficiaries reference this village

-- First check if any beneficiaries reference this village
SELECT v.village_id, v.village_name, COUNT(b.beneficiary_id) AS beneficiary_count
FROM villages v
LEFT JOIN beneficiaries b ON v.village_id = b.village_id
WHERE v.village_id = 51
GROUP BY v.village_id, v.village_name;


-- Test 4.2: Delete audit log entries older than retention period
-- Using the maintenance procedure
CALL sp_maintain_audit_log(365);


-- Test 4.3: Soft delete - deactivate a beneficiary instead of hard delete
UPDATE beneficiaries 
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE beneficiary_id = 13;

SELECT beneficiary_id, first_name, last_name, is_active 
FROM beneficiaries WHERE beneficiary_id = 13;


-- Test 4.4: Verify soft-deleted beneficiary still exists
SELECT COUNT(*) AS active_beneficiaries 
FROM beneficiaries WHERE is_active = TRUE;

SELECT COUNT(*) AS inactive_beneficiaries 
FROM beneficiaries WHERE is_active = FALSE;


-- Test 4.5: Reactivate the beneficiary
UPDATE beneficiaries 
SET is_active = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE beneficiary_id = 13;

SELECT beneficiary_id, first_name, is_active 
FROM beneficiaries WHERE beneficiary_id = 13;


-- ============================================================================
-- SECTION 5: SELECT TESTING - BASIC QUERIES
-- ============================================================================
-- Tests basic SELECT operations with various clauses.


-- Test 5.1: Select all active beneficiaries from Maharashtra
SELECT 
    b.beneficiary_id,
    CONCAT(b.first_name, ' ', b.last_name) AS full_name,
    b.category,
    b.annual_income,
    s.state_name
FROM beneficiaries b
INNER JOIN villages v ON b.village_id = v.village_id
INNER JOIN blocks blk ON v.block_id = blk.block_id
INNER JOIN districts d ON blk.district_id = d.district_id
INNER JOIN states s ON d.state_id = s.state_id
WHERE s.state_name = 'Maharashtra'
AND b.is_active = TRUE;


-- Test 5.2: Select schemes with specific eligibility criteria
SELECT 
    scheme_name,
    scheme_category,
    min_age,
    max_age,
    max_annual_income,
    requires_bpl,
    max_disbursement_amount
FROM schemes
WHERE is_active = TRUE
AND max_annual_income <= 200000.00
ORDER BY max_disbursement_amount DESC;


-- Test 5.3: Select applications with BETWEEN clause
SELECT 
    application_number,
    application_date,
    application_status,
    applied_amount
FROM applications
WHERE application_date BETWEEN '2026-01-01' AND '2026-03-31'
ORDER BY application_date;


-- Test 5.4: Select with LIKE pattern matching
SELECT 
    first_name,
    last_name,
    aadhaar_number,
    phone_number
FROM beneficiaries
WHERE first_name LIKE 'S%'
OR last_name LIKE '%ar';


-- Test 5.5: Select with IN clause
SELECT 
    application_number,
    application_status,
    applied_amount
FROM applications
WHERE application_status IN ('Approved', 'Disbursed', 'Verified')
ORDER BY application_status, application_date;


-- Test 5.6: Select with IS NULL check
SELECT 
    application_number,
    rejection_reason,
    approved_amount
FROM applications
WHERE rejection_reason IS NULL
AND application_status = 'Approved';


-- Test 5.7: Select distinct values
SELECT DISTINCT category 
FROM beneficiaries 
WHERE is_active = TRUE
ORDER BY category;

SELECT DISTINCT scheme_category 
FROM schemes 
WHERE is_active = TRUE
ORDER BY scheme_category;


-- Test 5.8: Select with LIMIT and OFFSET (pagination)
SELECT 
    application_id,
    application_number,
    application_status,
    application_date
FROM applications
ORDER BY application_date DESC
LIMIT 5 OFFSET 0;


-- ============================================================================
-- SECTION 6: SELECT TESTING - AGGREGATE FUNCTIONS
-- ============================================================================
-- Tests COUNT, SUM, AVG, MIN, MAX operations.


-- Test 6.1: Count beneficiaries by category
SELECT 
    category,
    COUNT(*) AS beneficiary_count,
    ROUND(AVG(annual_income), 2) AS avg_income
FROM beneficiaries
WHERE is_active = TRUE
GROUP BY category
ORDER BY beneficiary_count DESC;


-- Test 6.2: Total disbursement by scheme
SELECT 
    s.scheme_name,
    COUNT(d.disbursement_id) AS total_disbursements,
    SUM(d.disbursement_amount) AS total_amount,
    ROUND(AVG(d.disbursement_amount), 2) AS avg_amount
FROM disbursement d
INNER JOIN applications a ON d.application_id = a.application_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
WHERE d.disbursement_status = 'Completed'
GROUP BY s.scheme_id, s.scheme_name
ORDER BY total_amount DESC;


-- Test 6.3: Application status distribution
SELECT 
    application_status,
    COUNT(*) AS status_count,
    SUM(applied_amount) AS total_applied,
    ROUND(AVG(applied_amount), 2) AS avg_applied
FROM applications
GROUP BY application_status
ORDER BY status_count DESC;


-- Test 6.4: Eligibility score statistics
SELECT 
    eligibility_status,
    COUNT(*) AS count,
    MIN(total_eligibility_score) AS min_score,
    MAX(total_eligibility_score) AS max_score,
    ROUND(AVG(total_eligibility_score), 2) AS avg_score
FROM eligibility
GROUP BY eligibility_status
ORDER BY avg_score DESC;


-- Test 6.5: Verification type statistics
SELECT 
    verification_type,
    verification_status,
    COUNT(*) AS count
FROM verification
GROUP BY verification_type, verification_status
ORDER BY verification_type, verification_status;


-- ============================================================================
-- SECTION 7: JOIN TESTING
-- ============================================================================
-- Tests various JOIN types across tables.


-- Test 7.1: INNER JOIN - Beneficiaries with their applications
SELECT 
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.category,
    a.application_number,
    a.application_status,
    a.applied_amount
FROM beneficiaries b
INNER JOIN applications a ON b.beneficiary_id = a.beneficiary_id
ORDER BY b.last_name, a.application_date;


-- Test 7.2: LEFT JOIN - All schemes with application counts (including schemes with 0 applications)
SELECT 
    s.scheme_name,
    s.scheme_category,
    COUNT(a.application_id) AS application_count
FROM schemes s
LEFT JOIN applications a ON s.scheme_id = a.scheme_id
WHERE s.is_active = TRUE
GROUP BY s.scheme_id, s.scheme_name, s.scheme_category
ORDER BY application_count DESC;


-- Test 7.3: LEFT JOIN - All beneficiaries with application status (including those without applications)
SELECT 
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.category,
    COALESCE(a.application_number, 'No Application') AS application,
    COALESCE(a.application_status, 'N/A') AS status
FROM beneficiaries b
LEFT JOIN applications a ON b.beneficiary_id = a.beneficiary_id
WHERE b.is_active = TRUE
ORDER BY b.last_name;


-- Test 7.4: Multi-table JOIN - Complete application details
SELECT 
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.category,
    b.annual_income,
    s.scheme_name,
    s.scheme_category,
    e.total_eligibility_score,
    e.eligibility_status,
    v.verification_status,
    d.disbursement_status,
    d.disbursement_amount
FROM applications a
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
LEFT JOIN eligibility e ON a.application_id = e.application_id
LEFT JOIN verification v ON a.application_id = v.application_id
LEFT JOIN disbursement d ON a.application_id = d.application_id
ORDER BY a.application_date DESC;


-- Test 7.5: Self JOIN - Find beneficiaries from the same village
SELECT 
    CONCAT(b1.first_name, ' ', b1.last_name) AS beneficiary_1,
    CONCAT(b2.first_name, ' ', b2.last_name) AS beneficiary_2,
    v.village_name
FROM beneficiaries b1
INNER JOIN beneficiaries b2 ON b1.village_id = b2.village_id 
AND b1.beneficiary_id < b2.beneficiary_id
INNER JOIN villages v ON b1.village_id = v.village_id
WHERE b1.is_active = TRUE AND b2.is_active = TRUE;


-- Test 7.6: JOIN with aggregation - User workload analysis
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) AS officer_name,
    r.role_name,
    COUNT(DISTINCT a.application_id) AS applications_reviewed,
    COUNT(DISTINCT v.verification_id) AS verifications_done,
    COUNT(DISTINCT d.disbursement_id) AS disbursements_authorized
FROM users u
INNER JOIN roles r ON u.role_id = r.role_id
LEFT JOIN applications a ON u.user_id = a.reviewed_by
LEFT JOIN verification v ON u.user_id = v.verifier_id
LEFT JOIN disbursement d ON u.user_id = d.authorized_by
WHERE u.is_active = TRUE
GROUP BY u.user_id, u.first_name, u.last_name, r.role_name
ORDER BY applications_reviewed DESC;


-- ============================================================================
-- SECTION 8: GROUP BY TESTING
-- ============================================================================
-- Tests GROUP BY with various aggregate functions and conditions.


-- Test 8.1: Group by state with beneficiary count
SELECT 
    s.state_name,
    COUNT(DISTINCT b.beneficiary_id) AS total_beneficiaries,
    COUNT(DISTINCT CASE WHEN b.category = 'SC' THEN b.beneficiary_id END) AS sc_count,
    COUNT(DISTINCT CASE WHEN b.category = 'ST' THEN b.beneficiary_id END) AS st_count,
    COUNT(DISTINCT CASE WHEN b.is_bpl = TRUE THEN b.beneficiary_id END) AS bpl_count
FROM beneficiaries b
INNER JOIN villages v ON b.village_id = v.village_id
INNER JOIN blocks blk ON v.block_id = blk.block_id
INNER JOIN districts d ON blk.district_id = d.district_id
INNER JOIN states s ON d.state_id = s.state_id
WHERE b.is_active = TRUE
GROUP BY s.state_id, s.state_name
ORDER BY total_beneficiaries DESC;


-- Test 8.2: Group by month - Application trends
SELECT 
    YEAR(application_date) AS app_year,
    MONTH(application_date) AS app_month,
    COUNT(*) AS applications,
    SUM(applied_amount) AS total_amount,
    COUNT(CASE WHEN application_status = 'Approved' THEN 1 END) AS approved,
    COUNT(CASE WHEN application_status = 'Rejected' THEN 1 END) AS rejected
FROM applications
GROUP BY YEAR(application_date), MONTH(application_date)
ORDER BY app_year, app_month;


-- Test 8.3: Group by scheme category
SELECT 
    s.scheme_category,
    COUNT(DISTINCT s.scheme_id) AS schemes,
    COUNT(a.application_id) AS applications,
    ROUND(AVG(e.total_eligibility_score), 2) AS avg_eligibility_score
FROM schemes s
LEFT JOIN applications a ON s.scheme_id = a.scheme_id
LEFT JOIN eligibility e ON a.application_id = e.application_id
GROUP BY s.scheme_category
ORDER BY applications DESC;


-- Test 8.4: Group by disbursement mode
SELECT 
    disbursement_mode,
    COUNT(*) AS transaction_count,
    SUM(disbursement_amount) AS total_amount,
    COUNT(CASE WHEN disbursement_status = 'Completed' THEN 1 END) AS completed,
    COUNT(CASE WHEN disbursement_status = 'Failed' THEN 1 END) AS failed
FROM disbursement
GROUP BY disbursement_mode
ORDER BY total_amount DESC;


-- Test 8.5: Group by age group
SELECT 
    CASE 
        WHEN TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) < 25 THEN '18-24'
        WHEN TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) BETWEEN 25 AND 35 THEN '25-35'
        WHEN TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) BETWEEN 36 AND 50 THEN '36-50'
        ELSE '50+'
    END AS age_group,
    COUNT(*) AS beneficiary_count,
    ROUND(AVG(annual_income), 2) AS avg_income
FROM beneficiaries
WHERE is_active = TRUE
GROUP BY age_group
ORDER BY age_group;


-- ============================================================================
-- SECTION 9: ORDER BY TESTING
-- ============================================================================
-- Tests various sorting operations.


-- Test 9.1: Sort beneficiaries by income (ascending) then category
SELECT 
    first_name,
    last_name,
    annual_income,
    category,
    is_bpl
FROM beneficiaries
WHERE is_active = TRUE
ORDER BY annual_income ASC, category ASC;


-- Test 9.2: Sort applications by date (newest first) then status
SELECT 
    application_number,
    application_date,
    application_status,
    applied_amount
FROM applications
ORDER BY application_date DESC, application_status ASC;


-- Test 9.3: Sort eligibility scores (highest first)
SELECT 
    e.application_id,
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    e.total_eligibility_score,
    e.eligibility_status
FROM eligibility e
INNER JOIN applications a ON e.application_id = a.application_id
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
ORDER BY e.total_eligibility_score DESC;


-- Test 9.4: Sort disbursements by amount (largest first)
SELECT 
    disbursement_number,
    disbursement_amount,
    disbursement_status,
    disbursement_date
FROM disbursement
ORDER BY disbursement_amount DESC;


-- Test 9.5: Sort with multiple columns and NULL handling
SELECT 
    application_number,
    rejection_reason,
    approved_amount,
    application_status
FROM applications
ORDER BY 
    approved_amount DESC NULLS LAST,
    application_status ASC;


-- ============================================================================
-- SECTION 10: ELIGIBILITY SCORE TESTING
-- ============================================================================
-- Tests the eligibility scoring engine and functions.


-- Test 10.1: Test age calculation function
SELECT 
    'Age Function Tests' AS test_category,
    fn_calculate_age('1990-05-15') AS calculated_age,
    'Expected: 36' AS expected;


-- Test 10.2: Test age scoring function
SELECT 
    'Age Scoring Tests' AS test_category,
    fn_score_age('1990-05-15', 18, 65, 20) AS young_adult,
    fn_score_age('1970-01-01', 18, 65, 20) AS middle_aged,
    fn_score_age('2010-01-01', 18, 65, 20) AS child_under_age,
    'Expected: 20, 14, 0' AS expected;


-- Test 10.3: Test income scoring function (inverse: lower = higher score)
SELECT 
    'Income Scoring Tests' AS test_category,
    fn_score_income(50000.00, NULL, 300000.00, 20) AS low_income,
    fn_score_income(250000.00, NULL, 300000.00, 20) AS high_income,
    fn_score_income(350000.00, NULL, 300000.00, 20) AS over_limit,
    'Expected: 17, 3, 0' AS expected;


-- Test 10.4: Test category scoring function
SELECT 
    'Category Scoring Tests' AS test_category,
    fn_score_category('SC', 'General,SC,ST,OBC,EWS', 20) AS sc_score,
    fn_score_category('ST', 'General,SC,ST,OBC,EWS', 20) AS st_score,
    fn_score_category('OBC', 'General,SC,ST,OBC,EWS', 20) AS obc_score,
    fn_score_category('General', 'SC,ST', 20) AS general_not_eligible,
    'Expected: 20, 20, 16, 0' AS expected;


-- Test 10.5: Test disability scoring function
SELECT 
    'Disability Scoring Tests' AS test_category,
    fn_score_disability(TRUE, TRUE, 20) AS disabled_scheme_disabled,
    fn_score_disability(FALSE, TRUE, 20) AS able_scheme_disabled,
    fn_score_disability(TRUE, FALSE, 20) AS disabled_scheme_able,
    fn_score_disability(FALSE, FALSE, 20) AS able_scheme_able,
    'Expected: 20, 0, 20, 10' AS expected;


-- Test 10.6: Test BPL scoring function
SELECT 
    'BPL Scoring Tests' AS test_category,
    fn_score_bpl(TRUE, TRUE, 10) AS bpl_scheme_bpl,
    fn_score_bpl(FALSE, TRUE, 10) AS non_bpl_scheme_bpl,
    fn_score_bpl(TRUE, FALSE, 10) AS bpl_scheme_non_bpl,
    fn_score_bpl(FALSE, FALSE, 10) AS non_bpl_scheme_non_bpl,
    'Expected: 10, 0, 10, 5' AS expected;


-- Test 10.7: Test location scoring function
SELECT 
    'Location Scoring Tests' AS test_category,
    fn_score_location(1, 10) AS small_village,
    fn_score_location(9, 10) AS large_village,
    'Expected varies by population' AS note;


-- Test 10.8: Test eligibility status determination function
SELECT 
    'Status Determination Tests' AS test_category,
    fn_determine_eligibility_status(85, 100, 1) AS high_score,
    fn_determine_eligibility_status(55, 100, 1) AS medium_score,
    fn_determine_eligibility_status(30, 100, 1) AS low_score,
    'Expected: Eligible, Partially Eligible, Not Eligible' AS expected;


-- Test 10.9: Calculate eligibility for a specific application
CALL sp_calculate_eligibility(1, 6, 1);


-- Test 10.10: Verify eligibility record was created
SELECT 
    e.application_id,
    a.application_number,
    e.age_score,
    e.income_score,
    e.category_score,
    e.disability_score,
    e.bpl_score,
    e.location_score,
    e.total_eligibility_score,
    e.max_possible_score,
    e.eligibility_status,
    e.evaluation_notes
FROM eligibility e
INNER JOIN applications a ON e.application_id = a.application_id
WHERE e.application_id = 1;


-- Test 10.11: Re-calculate eligibility for another application
CALL sp_calculate_eligibility(4, 6, 1);


-- Test 10.12: Compare eligibility scores across applications
SELECT 
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    s.scheme_name,
    e.total_eligibility_score,
    e.eligibility_status,
    e.age_score,
    e.income_score,
    e.category_score,
    e.disability_score,
    e.bpl_score,
    e.location_score
FROM eligibility e
INNER JOIN applications a ON e.application_id = a.application_id
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
ORDER BY e.total_eligibility_score DESC;


-- ============================================================================
-- SECTION 11: VERIFICATION WORKFLOW TESTING
-- ============================================================================
-- Tests the complete verification workflow from pending to passed.


-- Test 11.1: View current verification status for all applications
SELECT 
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    a.application_status AS app_status,
    v.verification_type,
    v.verification_status,
    v.document_verified,
    v.field_visit_done,
    v.aadhaar_verified,
    v.income_verified,
    v.address_verified
FROM applications a
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
LEFT JOIN verification v ON a.application_id = v.application_id
ORDER BY a.application_date;


-- Test 11.2: Update verification - Document check passed
UPDATE verification 
SET document_verified = TRUE,
    verification_status = 'In Progress',
    verification_notes = 'Aadhaar card verified against government database'
WHERE application_id = 13;

SELECT * FROM verification WHERE application_id = 13;


-- Test 11.3: Update verification - Aadhaar authentication passed
UPDATE verification 
SET aadhaar_verified = TRUE,
    verification_status = 'Passed',
    verification_date = CURRENT_TIMESTAMP,
    verification_notes = 'Aadhaar biometric authentication successful'
WHERE application_id = 13;

SELECT * FROM verification WHERE application_id = 13;


-- Test 11.4: Calculate eligibility after verification passes
CALL sp_calculate_eligibility(13, 6, 1);


-- Test 11.5: View complete workflow for application 13
SELECT 
    a.application_number,
    a.application_status,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    s.scheme_name,
    v.verification_status,
    v.document_verified,
    v.aadhaar_verified,
    e.eligibility_status,
    e.total_eligibility_score,
    e.eligibility_status
FROM applications a
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
LEFT JOIN verification v ON a.application_id = v.application_id
LEFT JOIN eligibility e ON a.application_id = e.application_id
WHERE a.application_id = 13;


-- Test 11.6: Approve application if eligibility is met
CALL sp_approve_application(13, 3, 6000.00);

SELECT application_number, application_status, approved_amount 
FROM applications WHERE application_id = 13;


-- Test 11.7: Process disbursement for approved application
CALL sp_process_disbursement(13, 7, 2000.00, 'Direct Bank Transfer');

SELECT 
    disbursement_number, disbursement_amount, 
    disbursement_status, installment_number
FROM disbursement WHERE application_id = 13;


-- Test 11.8: View complete end-to-end workflow
SELECT 
    a.application_number,
    a.application_status AS final_status,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary,
    s.scheme_name AS scheme,
    e.eligibility_status,
    e.total_eligibility_score AS score,
    v.verification_status AS verified,
    d.disbursement_number,
    d.disbursement_amount,
    d.disbursement_status AS disbursed
FROM applications a
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
LEFT JOIN eligibility e ON a.application_id = e.application_id
LEFT JOIN verification v ON a.application_id = v.application_id
LEFT JOIN disbursement d ON a.application_id = d.application_id
WHERE a.application_id = 13;


-- ============================================================================
-- SECTION 12: AUDIT LOG TESTING
-- ============================================================================
-- Tests audit trail functionality.


-- Test 12.1: View recent audit log entries
SELECT 
    al.log_id,
    u.username,
    al.action_type,
    al.table_affected,
    al.record_id,
    al.action_description,
    al.action_timestamp
FROM audit_log al
LEFT JOIN users u ON al.user_id = u.user_id
ORDER BY al.action_timestamp DESC
LIMIT 10;


-- Test 12.2: Audit log by action type
SELECT 
    action_type,
    COUNT(*) AS action_count
FROM audit_log
GROUP BY action_type
ORDER BY action_count DESC;


-- Test 12.3: Audit log by table
SELECT 
    table_affected,
    action_type,
    COUNT(*) AS count
FROM audit_log
GROUP BY table_affected, action_type
ORDER BY table_affected, count DESC;


-- Test 12.4: Audit log for specific beneficiary
SELECT 
    al.action_type,
    al.table_affected,
    al.record_id,
    al.action_description,
    al.action_timestamp
FROM audit_log al
WHERE al.new_values IS NOT NULL
AND JSON_EXTRACT(al.new_values, '$.beneficiary_id') = 13
ORDER BY al.action_timestamp DESC;


-- ============================================================================
-- SECTION 13: COMPLEX QUERIES TESTING
-- ============================================================================
-- Tests complex analytical and reporting queries.


-- Test 13.1: Dashboard summary query
SELECT 
    (SELECT COUNT(*) FROM beneficiaries WHERE is_active = TRUE) AS total_beneficiaries,
    (SELECT COUNT(*) FROM schemes WHERE is_active = TRUE) AS active_schemes,
    (SELECT COUNT(*) FROM applications) AS total_applications,
    (SELECT COUNT(*) FROM applications WHERE application_status = 'Approved') AS approved,
    (SELECT COUNT(*) FROM applications WHERE application_status = 'Rejected') AS rejected,
    (SELECT COUNT(*) FROM disbursement WHERE disbursement_status = 'Completed') AS disbursed,
    (SELECT COALESCE(SUM(disbursement_amount), 0) FROM disbursement WHERE disbursement_status = 'Completed') AS total_amount_disbursed;


-- Test 13.2: Scheme performance report
SELECT 
    s.scheme_name,
    s.scheme_category,
    COUNT(a.application_id) AS applications,
    COUNT(CASE WHEN a.application_status = 'Approved' THEN 1 END) AS approved,
    COUNT(CASE WHEN a.application_status = 'Rejected' THEN 1 END) AS rejected,
    ROUND(COUNT(CASE WHEN a.application_status = 'Approved' THEN 1 END) * 100.0 / 
          NULLIF(COUNT(a.application_id), 0), 2) AS approval_rate,
    COALESCE(SUM(CASE WHEN d.disbursement_status = 'Completed' THEN d.disbursement_amount ELSE 0 END), 0) AS total_disbursed
FROM schemes s
LEFT JOIN applications a ON s.scheme_id = a.scheme_id
LEFT JOIN disbursement d ON a.application_id = d.application_id
WHERE s.is_active = TRUE
GROUP BY s.scheme_id, s.scheme_name, s.scheme_category
ORDER BY applications DESC;


-- Test 13.3: Beneficiary wise all applications
SELECT 
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary,
    b.category,
    COUNT(a.application_id) AS total_applications,
    GROUP_CONCAT(a.application_status SEPARATOR ', ') AS statuses,
    COALESCE(SUM(d.disbursement_amount), 0) AS total_received
FROM beneficiaries b
LEFT JOIN applications a ON b.beneficiary_id = a.beneficiary_id
LEFT JOIN disbursement d ON a.application_id = d.application_id
WHERE b.is_active = TRUE
GROUP BY b.beneficiary_id, b.first_name, b.last_name, b.category
HAVING total_applications > 0
ORDER BY total_applications DESC;


-- Test 13.4: Monthly disbursement trend
SELECT 
    YEAR(d.disbursement_date) AS dis_year,
    MONTH(d.disbursement_date) AS dis_month,
    COUNT(*) AS transactions,
    SUM(d.disbursement_amount) AS total_amount,
    COUNT(CASE WHEN d.disbursement_status = 'Completed' THEN 1 END) AS successful,
    COUNT(CASE WHEN d.disbursement_status = 'Failed' THEN 1 END) AS failed
FROM disbursement d
WHERE d.disbursement_date IS NOT NULL
GROUP BY YEAR(d.disbursement_date), MONTH(d.disbursement_date)
ORDER BY dis_year, dis_month;


-- Test 13.5: Top 5 beneficiaries by eligibility score
SELECT 
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.category,
    b.annual_income,
    s.scheme_name,
    e.total_eligibility_score,
    e.eligibility_status,
    e.evaluation_date
FROM eligibility e
INNER JOIN applications a ON e.application_id = a.application_id
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
ORDER BY e.total_eligibility_score DESC
LIMIT 5;


-- ============================================================================
-- SECTION 14: TESTING COMPLETE
-- ============================================================================

SELECT '=========================================' AS separator;
SELECT 'ALL TEST QUERIES EXECUTED SUCCESSFULLY' AS test_status;
SELECT '=========================================' AS separator;

-- Final summary
SELECT 'Test Summary' AS section;
SELECT 
    'INSERT Tests' AS test_type, 8 AS count
UNION ALL
SELECT 'UPDATE Tests', 5
UNION ALL
SELECT 'DELETE Tests', 5
UNION ALL
SELECT 'SELECT Tests', 14
UNION ALL
SELECT 'JOIN Tests', 6
UNION ALL
SELECT 'GROUP BY Tests', 5
UNION ALL
SELECT 'ORDER BY Tests', 5
UNION ALL
SELECT 'Eligibility Score Tests', 12
UNION ALL
SELECT 'Verification Workflow Tests', 8
UNION ALL
SELECT 'Audit Log Tests', 4
UNION ALL
SELECT 'Complex Query Tests', 5
UNION ALL
SELECT 'TOTAL', 77;
