-- ============================================================================
-- GOVERNMENT BENEFICIARY DISBURSEMENT SYSTEM
-- Eligibility Scoring Engine
--
-- Description: This script implements the complete eligibility scoring logic
--              for the Government Beneficiary Disbursement System. It includes
--              stored procedures, stored functions, CASE-based scoring logic,
--              and mechanisms to store eligibility scores and statuses.
--
-- Compatible With: MySQL 8.0+
-- Author: Database & Eligibility Scoring Module
-- Version: 1.0
-- ============================================================================

USE gov_beneficiary_db;


-- ============================================================================
-- SECTION 1: ELIGIBILITY SCORING CONFIGURATION TABLE
-- ============================================================================
-- This table stores configurable scoring weights and thresholds.
-- Allows modification of scoring logic without changing stored procedures.


DROP TABLE IF EXISTS eligibility_scoring_config;

CREATE TABLE eligibility_scoring_config (
    config_id INT NOT NULL AUTO_INCREMENT,
    config_name VARCHAR(100) NOT NULL,
    config_description TEXT NULL,
    age_weight INT NOT NULL DEFAULT 20,
    income_weight INT NOT NULL DEFAULT 20,
    category_weight INT NOT NULL DEFAULT 20,
    disability_weight INT NOT NULL DEFAULT 20,
    bpl_weight INT NOT NULL DEFAULT 10,
    location_weight INT NOT NULL DEFAULT 10,
    eligible_threshold INT NOT NULL DEFAULT 70,
    partially_eligible_threshold INT NOT NULL DEFAULT 40,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (config_id),
    CONSTRAINT uq_config_name UNIQUE (config_name),
    CONSTRAINT chk_weights_positive CHECK (age_weight >= 0 AND income_weight >= 0
        AND category_weight >= 0
        AND disability_weight >= 0
        AND bpl_weight >= 0
        AND location_weight >= 0),
    CONSTRAINT chk_thresholds_valid CHECK (eligible_threshold > partially_eligible_threshold
        AND eligible_threshold <= 100
        AND partially_eligible_threshold >= 0)
)  ENGINE=INNODB COMMENT='Configurable scoring weights and eligibility thresholds';


-- Insert default scoring configuration
INSERT INTO eligibility_scoring_config (
    config_name, config_description,
    age_weight, income_weight, category_weight,
    disability_weight, bpl_weight, location_weight,
    eligible_threshold, partially_eligible_threshold
) VALUES
('Default Scoring', 'Standard eligibility scoring configuration for all schemes',
 20, 20, 20, 20, 10, 10, 70, 40),
('Agriculture Scheme Scoring', 'Scoring configuration optimized for agriculture-based schemes',
 15, 25, 15, 5, 25, 15, 65, 35),
('Education Scheme Scoring', 'Scoring configuration optimized for education schemes',
 25, 15, 20, 5, 20, 15, 60, 30),
('Disability Scheme Scoring', 'Scoring configuration for disability-focused schemes',
 15, 15, 15, 25, 15, 15, 65, 35),
('Housing Scheme Scoring', 'Scoring configuration for housing assistance schemes',
 15, 25, 15, 10, 25, 10, 70, 40);


-- ============================================================================
-- SECTION 2: STORED FUNCTIONS
-- ============================================================================
-- Reusable functions for individual scoring components.


-- ============================================================================
-- Function: fn_calculate_age
-- Purpose: Calculates age from date of birth
-- Returns: Integer age in years
-- ============================================================================

DROP FUNCTION IF EXISTS fn_calculate_age;

DELIMITER //

CREATE FUNCTION fn_calculate_age(p_date_of_birth DATE)
RETURNS INT
DETERMINISTIC

BEGIN
    DECLARE v_age INT;
    SET v_age = TIMESTAMPDIFF(YEAR, p_date_of_birth, CURDATE());
    RETURN v_age;
END //

DELIMITER ;


-- ============================================================================
-- Function: fn_score_age
-- Purpose: Calculates eligibility score based on age
-- Parameters: Date of birth, minimum age, maximum age, max_score
-- Returns: Integer score (0 to max_score)
-- ============================================================================

DROP FUNCTION IF EXISTS fn_score_age;

DELIMITER //

CREATE FUNCTION fn_score_age(
    p_date_of_birth DATE,
    p_min_age INT,
    p_max_age INT,
    p_max_score INT
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_age INT;
    DECLARE v_score INT DEFAULT 0;
    
    SET v_age = fn_calculate_age(p_date_of_birth);
    
    -- Age scoring logic using CASE
    -- Perfect score if age is within the ideal range (middle 60%)
    -- Reduced score if age is at boundaries
    -- Zero score if age is outside eligible range
    
    IF p_min_age IS NULL OR p_max_age IS NULL THEN
        -- No age restriction, full score
        SET v_score = p_max_score;
    ELSEIF v_age < p_min_age OR v_age > p_max_age THEN
        -- Outside eligible range
        SET v_score = 0;
    ELSE
        -- Age within range, score based on proximity to ideal range
        SET v_score = CASE
            WHEN v_age BETWEEN (p_min_age + FLOOR((p_max_age - p_min_age) * 0.2)) 
                          AND (p_max_age - FLOOR((p_max_age - p_min_age) * 0.2))
                THEN p_max_score
            WHEN v_age BETWEEN p_min_age 
                          AND (p_min_age + FLOOR((p_max_age - p_min_age) * 0.2) - 1)
                THEN FLOOR(p_max_score * 0.7)
            WHEN v_age BETWEEN (p_max_age - FLOOR((p_max_age - p_min_age) * 0.2) + 1) 
                          AND p_max_age
                THEN FLOOR(p_max_score * 0.7)
            ELSE p_max_score
        END;
    END IF;
    
    RETURN v_score;
END //

DELIMITER ;


-- ============================================================================
-- Function: fn_score_income
-- Purpose: Calculates eligibility score based on annual income
-- Parameters: Annual income, minimum income, maximum income, max_score
-- Returns: Integer score (0 to max_score)
-- ============================================================================

DROP FUNCTION IF EXISTS fn_score_income;

DELIMITER //

CREATE FUNCTION fn_score_income(
    p_annual_income DECIMAL(12,2),
    p_min_income DECIMAL(12,2),
    p_max_income DECIMAL(12,2),
    p_max_score INT
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_score INT DEFAULT 0;
    DECLARE v_income_ratio DECIMAL(5,2);
    
    -- Income scoring logic: lower income = higher score
    -- This is inverse scoring because poor beneficiaries need more support
    
    IF p_max_income IS NULL THEN
        -- No income restriction, full score
        SET v_score = p_max_score;
    ELSEIF p_annual_income > p_max_income THEN
        -- Income exceeds maximum threshold
        SET v_score = 0;
    ELSEIF p_min_income IS NOT NULL AND p_annual_income < p_min_income THEN
        -- Income below minimum (very poor), highest score
        SET v_score = p_max_score;
    ELSE
        -- Calculate score based on income position within range
        -- Lower income = higher score (inverse proportion)
        SET v_income_ratio = 1.00 - ((p_annual_income - COALESCE(p_min_income, 0)) / 
                                      (p_max_income - COALESCE(p_min_income, 0)));
        SET v_score = GREATEST(0, LEAST(p_max_score, FLOOR(p_max_score * v_income_ratio)));
    END IF;
    
    RETURN v_score;
END //

DELIMITER ;


-- ============================================================================
-- Function: fn_score_category
-- Purpose: Calculates eligibility score based on social category
-- Parameters: Beneficiary category, eligible categories, max_score
-- Returns: Integer score (0 to max_score)
-- ============================================================================

DROP FUNCTION IF EXISTS fn_score_category;

DELIMITER //

CREATE FUNCTION fn_score_category(
    p_category VARCHAR(20),
    p_eligible_categories VARCHAR(255),
    p_max_score INT
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_score INT DEFAULT 0;
    
    -- Category scoring: marginalized categories get higher scores
    -- SC/ST get highest, OBC gets medium, General gets lower if eligible
    
    IF NOT FIND_IN_SET(p_category, p_eligible_categories) THEN
        -- Category not eligible for this scheme
        SET v_score = 0;
    ELSE
        -- Score based on category priority
        SET v_score = CASE p_category
            WHEN 'SC'  THEN p_max_score                              -- Scheduled Caste: Full score
            WHEN 'ST'  THEN p_max_score                              -- Scheduled Tribe: Full score
            WHEN 'OBC' THEN FLOOR(p_max_score * 0.80)                -- Other Backward Class: 80%
            WHEN 'EWS' THEN FLOOR(p_max_score * 0.75)                -- Economically Weaker Section: 75%
            WHEN 'General' THEN FLOOR(p_max_score * 0.50)            -- General: 50%
            ELSE FLOOR(p_max_score * 0.50)                           -- Default
        END;
    END IF;
    
    RETURN v_score;
END //

DELIMITER ;


-- ============================================================================
-- Function: fn_score_disability
-- Purpose: Calculates eligibility score based on disability status
-- Parameters: Disability status, scheme requires disability, max_score
-- Returns: Integer score (0 to max_score)
-- ============================================================================

DROP FUNCTION IF EXISTS fn_score_disability;

DELIMITER //

CREATE FUNCTION fn_score_disability(
    p_disability_status BOOLEAN,
    p_requires_disability BOOLEAN,
    p_max_score INT
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_score INT DEFAULT 0;
    
    -- Disability scoring logic:
    -- If scheme requires disability and beneficiary has it: Full score
    -- If scheme does not require disability: Neutral score (half)
    -- If scheme requires disability but beneficiary does not: Zero
    
    SET v_score = CASE
        WHEN p_requires_disability = TRUE AND p_disability_status = TRUE 
            THEN p_max_score                                        -- Disability scheme + disabled: Full
        WHEN p_requires_disability = TRUE AND p_disability_status = FALSE 
            THEN 0                                                  -- Disability scheme but not disabled: Zero
        WHEN p_requires_disability = FALSE AND p_disability_status = TRUE 
            THEN p_max_score                                        -- Non-disability scheme + disabled: Bonus full
        ELSE FLOOR(p_max_score * 0.5)                               -- Non-disability scheme + not disabled: Half
    END;
    
    RETURN v_score;
END //

DELIMITER ;


-- ============================================================================
-- Function: fn_score_bpl
-- Purpose: Calculates eligibility score based on BPL status
-- Parameters: BPL status, scheme requires BPL, max_score
-- Returns: Integer score (0 to max_score)
-- ============================================================================

DROP FUNCTION IF EXISTS fn_score_bpl;

DELIMITER //

CREATE FUNCTION fn_score_bpl(
    p_is_bpl BOOLEAN,
    p_requires_bpl BOOLEAN,
    p_max_score INT
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_score INT DEFAULT 0;
    
    -- BPL scoring logic:
    -- If scheme requires BPL and beneficiary is BPL: Full score
    -- If scheme does not require BPL: Neutral score (half)
    -- If scheme requires BPL but beneficiary is not: Zero
    
    SET v_score = CASE
        WHEN p_requires_bpl = TRUE AND p_is_bpl = TRUE 
            THEN p_max_score                                        -- BPL scheme + BPL holder: Full
        WHEN p_requires_bpl = TRUE AND p_is_bpl = FALSE 
            THEN 0                                                  -- BPL scheme but not BPL: Zero
        WHEN p_requires_bpl = FALSE AND p_is_bpl = TRUE 
            THEN p_max_score                                        -- Non-BPL scheme + BPL holder: Bonus
        ELSE FLOOR(p_max_score * 0.5)                               -- Non-BPL scheme + not BPL: Half
    END;
    
    RETURN v_score;
END //

DELIMITER ;


-- ============================================================================
-- Function: fn_score_location
-- Purpose: Calculates eligibility score based on beneficiary location
-- Parameters: Village ID, max_score
-- Returns: Integer score (0 to max_score)
-- ============================================================================

DROP FUNCTION IF EXISTS fn_score_location;

DELIMITER //

CREATE FUNCTION fn_score_location(
    p_village_id INT,
    p_max_score INT
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_population INT;
    DECLARE v_score INT DEFAULT 0;
    
    -- Location scoring: beneficiaries from smaller/remote villages get higher scores
    -- This prioritizes rural and underserved areas
    
    SELECT COALESCE(population, 0) INTO v_population
    FROM villages WHERE village_id = p_village_id;
    
    SET v_score = CASE
        WHEN v_population <= 5000 THEN p_max_score                  -- Very small village: Full
        WHEN v_population <= 10000 THEN FLOOR(p_max_score * 0.90)   -- Small village: 90%
        WHEN v_population <= 20000 THEN FLOOR(p_max_score * 0.75)   -- Medium village: 75%
        WHEN v_population <= 30000 THEN FLOOR(p_max_score * 0.60)   -- Large village: 60%
        ELSE FLOOR(p_max_score * 0.50)                               -- Very large/urban: 50%
    END;
    
    RETURN v_score;
END //

DELIMITER ;


-- ============================================================================
-- Function: fn_determine_eligibility_status
-- Purpose: Determines eligibility status based on total score and thresholds
-- Parameters: Total score, max score, config ID
-- Returns: VARCHAR eligibility status
-- ============================================================================

DROP FUNCTION IF EXISTS fn_determine_eligibility_status;

DELIMITER //

CREATE FUNCTION fn_determine_eligibility_status(
    p_total_score INT,
    p_max_score INT,
    p_config_id INT
)
RETURNS VARCHAR(20)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_eligible_threshold INT;
    DECLARE v_partially_eligible_threshold INT;
    DECLARE v_percentage DECIMAL(5,2);
    DECLARE v_status VARCHAR(20);
    
    -- Get thresholds from configuration
    SELECT eligible_threshold, partially_eligible_threshold
    INTO v_eligible_threshold, v_partially_eligible_threshold
    FROM eligibility_scoring_config
    WHERE config_id = p_config_id AND is_active = TRUE;
    
    -- Default thresholds if config not found
    SET v_eligible_threshold = COALESCE(v_eligible_threshold, 70);
    SET v_partially_eligible_threshold = COALESCE(v_partially_eligible_threshold, 40);
    
    -- Calculate percentage
    SET v_percentage = (p_total_score / p_max_score) * 100;
    
    -- Determine status using CASE
    SET v_status = CASE
        WHEN v_percentage >= v_eligible_threshold THEN 'Eligible'
        WHEN v_percentage >= v_partially_eligible_threshold THEN 'Partially Eligible'
        ELSE 'Not Eligible'
    END;
    
    RETURN v_status;
END //

DELIMITER ;


-- ============================================================================
-- SECTION 3: CORE SCORING PROCEDURE
-- ============================================================================
-- Main stored procedure that calculates complete eligibility for an application.


-- ============================================================================
-- Procedure: sp_calculate_eligibility
-- Purpose: Calculates and stores complete eligibility score for an application
-- Parameters: p_application_id, p_evaluated_by, p_config_id
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_calculate_eligibility;

DELIMITER //

CREATE PROCEDURE sp_calculate_eligibility(
    IN p_application_id INT,
    IN p_evaluated_by INT,
    IN p_config_id INT
)
BEGIN
    -- Local variables for beneficiary data
    DECLARE v_beneficiary_id INT;
    DECLARE v_village_id INT;
    DECLARE v_date_of_birth DATE;
    DECLARE v_annual_income DECIMAL(12,2);
    DECLARE v_category VARCHAR(20);
    DECLARE v_disability_status BOOLEAN;
    DECLARE v_is_bpl BOOLEAN;
    
    -- Local variables for scheme data
    DECLARE v_min_age INT;
    DECLARE v_max_age INT;
    DECLARE v_min_income DECIMAL(12,2);
    DECLARE v_max_income DECIMAL(12,2);
    DECLARE v_eligible_categories VARCHAR(255);
    DECLARE v_requires_disability BOOLEAN;
    DECLARE v_requires_bpl BOOLEAN;
    
    -- Local variables for scoring
    DECLARE v_age_weight INT;
    DECLARE v_income_weight INT;
    DECLARE v_category_weight INT;
    DECLARE v_disability_weight INT;
    DECLARE v_bpl_weight INT;
    DECLARE v_location_weight INT;
    DECLARE v_age_score INT;
    DECLARE v_income_score INT;
    DECLARE v_category_score INT;
    DECLARE v_disability_score INT;
    DECLARE v_bpl_score INT;
    DECLARE v_location_score INT;
    DECLARE v_total_score INT;
    DECLARE v_max_possible INT;
    DECLARE v_eligibility_status VARCHAR(20);
    DECLARE v_evaluation_notes TEXT;
    
    -- Exit handler for errors
   DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN

    INSERT INTO eligibility (
        application_id,
        evaluated_by,
        eligibility_status,
        evaluation_notes,
        total_eligibility_score,
        max_possible_score
    ) VALUES (
        p_application_id,
        p_evaluated_by,
        'Pending Review',
        'Error during evaluation',
        0,
        100
    );

END;
    -- ============================================================
    -- STEP 2: Fetch beneficiary details
    -- ============================================================
    
    SELECT 
        b.beneficiary_id, b.village_id, b.date_of_birth,
        b.annual_income, b.category, b.disability_status, b.is_bpl
    INTO 
        v_beneficiary_id, v_village_id, v_date_of_birth,
        v_annual_income, v_category, v_disability_status, v_is_bpl
    FROM applications a
    INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
    WHERE a.application_id = p_application_id;
    
    -- ============================================================
    -- STEP 3: Fetch scheme eligibility criteria
    -- ============================================================
    
    SELECT 
        s.min_age, s.max_age, s.min_annual_income, s.max_annual_income,
        s.eligible_categories, s.requires_disability, s.requires_bpl
    INTO 
        v_min_age, v_max_age, v_min_income, v_max_income,
        v_eligible_categories, v_requires_disability, v_requires_bpl
    FROM applications a
    INNER JOIN schemes s ON a.scheme_id = s.scheme_id
    WHERE a.application_id = p_application_id;
    
    -- ============================================================
    -- STEP 4: Fetch scoring weights from configuration
    -- ============================================================
    
    SELECT 
        age_weight, income_weight, category_weight,
        disability_weight, bpl_weight, location_weight
    INTO 
        v_age_weight, v_income_weight, v_category_weight,
        v_disability_weight, v_bpl_weight, v_location_weight
    FROM eligibility_scoring_config
    WHERE config_id = p_config_id AND is_active = TRUE
    LIMIT 1;
    
    -- Default weights if config not found
    SET v_age_weight = COALESCE(v_age_weight, 20);
    SET v_income_weight = COALESCE(v_income_weight, 20);
    SET v_category_weight = COALESCE(v_category_weight, 20);
    SET v_disability_weight = COALESCE(v_disability_weight, 20);
    SET v_bpl_weight = COALESCE(v_bpl_weight, 10);
    SET v_location_weight = COALESCE(v_location_weight, 10);
    
    -- ============================================================
    -- STEP 5: Calculate individual component scores
    -- ============================================================
    
    -- Age Score
    SET v_age_score = fn_score_age(v_date_of_birth, v_min_age, v_max_age, v_age_weight);
    
    -- Income Score (inverse: lower income = higher score)
    SET v_income_score = fn_score_income(v_annual_income, v_min_income, v_max_income, v_income_weight);
    
    -- Category Score
    SET v_category_score = fn_score_category(v_category, v_eligible_categories, v_category_weight);
    
    -- Disability Score
    SET v_disability_score = fn_score_disability(v_disability_status, v_requires_disability, v_disability_weight);
    
    -- BPL Score
    SET v_bpl_score = fn_score_bpl(v_is_bpl, v_requires_bpl, v_bpl_weight);
    
    -- Location Score
    SET v_location_score = fn_score_location(v_village_id, v_location_weight);
    
    -- ============================================================
    -- STEP 6: Calculate total score and determine status
    -- ============================================================
    
    SET v_total_score = v_age_score + v_income_score + v_category_score + 
                        v_disability_score + v_bpl_score + v_location_score;
    
    SET v_max_possible = v_age_weight + v_income_weight + v_category_weight + 
                         v_disability_weight + v_bpl_weight + v_location_weight;
    
    SET v_eligibility_status = fn_determine_eligibility_status(
        v_total_score, v_max_possible, p_config_id
    );
    
    -- ============================================================
    -- STEP 7: Generate evaluation notes
    -- ============================================================
    
    SET v_evaluation_notes = CONCAT(
        'Age Score: ', v_age_score, '/', v_age_weight,
        ' | Income Score: ', v_income_score, '/', v_income_weight,
        ' | Category Score: ', v_category_score, '/', v_category_weight,
        ' | Disability Score: ', v_disability_score, '/', v_disability_weight,
        ' | BPL Score: ', v_bpl_score, '/', v_bpl_weight,
        ' | Location Score: ', v_location_score, '/', v_location_weight,
        ' | Total: ', v_total_score, '/', v_max_possible,
        ' (', ROUND((v_total_score / v_max_possible) * 100, 1), '%)'
    );
    
    -- ============================================================
    -- STEP 8: Delete existing eligibility record if re-evaluating
    -- ============================================================
    
    DELETE FROM eligibility WHERE application_id = p_application_id;
    
    -- ============================================================
    -- STEP 9: Store eligibility result
    -- ============================================================
    
    INSERT INTO eligibility (
        application_id, evaluated_by, evaluation_date,
        age_score, income_score, category_score, disability_score,
        bpl_score, location_score, total_eligibility_score,
        max_possible_score, eligibility_status, evaluation_notes
    ) VALUES (
        p_application_id, p_evaluated_by, CURRENT_TIMESTAMP,
        v_age_score, v_income_score, v_category_score, v_disability_score,
        v_bpl_score, v_location_score, v_total_score,
        v_max_possible, v_eligibility_status, v_evaluation_notes
    );
    
    -- ============================================================
    -- STEP 10: Update application status
    -- ============================================================
    
    UPDATE applications 
    SET application_status = 'Eligibility Checked',
        reviewed_by = p_evaluated_by,
        reviewed_at = CURRENT_TIMESTAMP
    WHERE application_id = p_application_id 
    AND application_status IN ('Submitted', 'Under Review');
    
    -- ============================================================
    -- STEP 11: Log the evaluation in audit trail
    -- ============================================================
    
    INSERT INTO audit_log (
        user_id, action_type, table_affected, record_id,
        new_values, action_description
    ) VALUES (
        p_evaluated_by, 'UPDATE', 'eligibility', p_application_id,
        JSON_OBJECT(
            'total_score', v_total_score,
            'max_possible', v_max_possible,
            'eligibility_status', v_eligibility_status
        ),
        CONCAT('Eligibility evaluated for application ID ', p_application_id, 
               ': ', v_eligibility_status, ' (', v_total_score, '/', v_max_possible, ')')
    );
    
    -- Return the result
    SELECT 
        p_application_id AS application_id,
        v_total_score AS total_score,
        v_max_possible AS max_possible,
        ROUND((v_total_score / v_max_possible) * 100, 1) AS eligibility_percentage,
        v_eligibility_status AS eligibility_status,
        v_evaluation_notes AS evaluation_notes;
    
END //

DELIMITER ;


-- ============================================================================
-- SECTION 4: BATCH SCORING PROCEDURE
-- ============================================================================
-- Processes eligibility for multiple applications at once.


-- ============================================================================
-- Procedure: sp_batch_calculate_eligibility
-- Purpose: Calculates eligibility for all pending applications
-- Parameters: p_config_id (optional, defaults to 1)
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_batch_calculate_eligibility;

DELIMITER //

CREATE PROCEDURE sp_batch_calculate_eligibility(
    IN p_config_id INT
)
BEGIN
    DECLARE v_application_id INT;
    DECLARE v_done INT DEFAULT FALSE;
    DECLARE v_processed_count INT DEFAULT 0;
    DECLARE v_error_count INT DEFAULT 0;
    
    -- Cursor for all pending applications
    DECLARE cur_pending_apps CURSOR FOR
        SELECT application_id 
        FROM applications 
        WHERE application_status IN ('Submitted', 'Under Review')
        ORDER BY application_date ASC;
    
    -- Handler for end of cursor
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;
    
    -- Default config
    SET p_config_id = COALESCE(p_config_id, 1);
    
    -- Open cursor and process each application
    OPEN cur_pending_apps;
    
    read_loop: LOOP
        FETCH cur_pending_apps INTO v_application_id;
        IF v_done THEN
            LEAVE read_loop;
        END IF;
        
        -- Try to calculate eligibility for each application
        BEGIN
            DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
            BEGIN
                SET v_error_count = v_error_count + 1;
            END;
            
            CALL sp_calculate_eligibility(v_application_id, NULL, p_config_id);
            SET v_processed_count = v_processed_count + 1;
        END;
        
    END LOOP;
    
    CLOSE cur_pending_apps;
    
    -- Return batch processing summary
    SELECT 
        v_processed_count AS applications_processed,
        v_error_count AS applications_failed,
        v_processed_count + v_error_count AS total_attempted;
    
END //

DELIMITER ;


-- ============================================================================
-- SECTION 5: SCORING REPORT PROCEDURE
-- ============================================================================
-- Generates detailed eligibility scoring reports.


-- ============================================================================
-- Procedure: sp_eligibility_scoring_report
-- Purpose: Generates comprehensive eligibility scoring report
-- Parameters: p_scheme_id (optional), p_status (optional)
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_eligibility_scoring_report;

DELIMITER //

CREATE PROCEDURE sp_eligibility_scoring_report(
    IN p_scheme_id INT,
    IN p_status VARCHAR(20)
)
BEGIN
    -- Return detailed scoring report
    SELECT 
        a.application_id,
        a.application_number,
        CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
        b.aadhaar_number,
        b.category,
        b.annual_income,
        TIMESTAMPDIFF(YEAR, b.date_of_birth, CURDATE()) AS age,
        b.disability_status,
        b.is_bpl,
        s.scheme_name,
        s.scheme_category,
        e.age_score,
        e.income_score,
        e.category_score,
        e.disability_score,
        e.bpl_score,
        e.location_score,
        e.total_eligibility_score,
        e.max_possible_score,
        ROUND((e.total_eligibility_score / e.max_possible_score) * 100, 1) AS eligibility_percentage,
        e.eligibility_status,
        e.evaluation_date,
        e.evaluation_notes
    FROM eligibility e
    INNER JOIN applications a ON e.application_id = a.application_id
    INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
    INNER JOIN schemes s ON a.scheme_id = s.scheme_id
    WHERE (p_scheme_id IS NULL OR a.scheme_id = p_scheme_id)
    AND (p_status IS NULL OR e.eligibility_status = p_status)
    ORDER BY e.total_eligibility_score DESC;
    
END //

DELIMITER ;


-- ============================================================================
-- SECTION 6: HELPER PROCEDURES FOR APPLICATION WORKFLOW
-- ============================================================================
-- Procedures for managing application status transitions.


-- ============================================================================
-- Procedure: sp_approve_application
-- Purpose: Approves an application after eligibility check
-- Parameters: p_application_id, p_approved_by, p_approved_amount
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_approve_application;

DELIMITER //

CREATE PROCEDURE sp_approve_application(
    IN p_application_id INT,
    IN p_approved_by INT,
    IN p_approved_amount DECIMAL(12,2)
)
BEGIN
    DECLARE v_current_status VARCHAR(30);
    DECLARE v_eligibility_status VARCHAR(20);
    
    -- Get current application status
    SELECT application_status INTO v_current_status
    FROM applications WHERE application_id = p_application_id;
    
    -- Check eligibility status
    SELECT eligibility_status INTO v_eligibility_status
    FROM eligibility WHERE application_id = p_application_id;
    
    -- Validate status transition
    IF v_current_status != 'Eligibility Checked' AND v_current_status != 'Verified' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Application must be eligibility checked or verified before approval';
    END IF;
    
    -- Validate eligibility
    IF v_eligibility_status = 'Not Eligible' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot approve application with Not Eligible status';
    END IF;
    
    -- Update application
    UPDATE applications 
    SET application_status = 'Approved',
        approved_amount = p_approved_amount,
        reviewed_by = p_approved_by,
        reviewed_at = CURRENT_TIMESTAMP
    WHERE application_id = p_application_id;
    
    -- Log the approval
    INSERT INTO audit_log (
        user_id, action_type, table_affected, record_id,
        new_values, action_description
    ) VALUES (
        p_approved_by, 'APPROVE', 'applications', p_application_id,
        JSON_OBJECT('application_status', 'Approved', 'approved_amount', p_approved_amount),
        CONCAT('Application approved with amount Rs ', p_approved_amount)
    );
    
    SELECT 'Application approved successfully' AS result;
    
END //

DELIMITER ;


-- ============================================================================
-- Procedure: sp_reject_application
-- Purpose: Rejects an application with reason
-- Parameters: p_application_id, p_rejected_by, p_rejection_reason
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_reject_application;

DELIMITER //

CREATE PROCEDURE sp_reject_application(
    IN p_application_id INT,
    IN p_rejected_by INT,
    IN p_rejection_reason TEXT
)
BEGIN
    DECLARE v_current_status VARCHAR(30);
    
    -- Get current status
    SELECT application_status INTO v_current_status
    FROM applications WHERE application_id = p_application_id;
    
    -- Validate status
    IF v_current_status IN ('Disbursed', 'Cancelled') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot reject application that is already disbursed or cancelled';
    END IF;
    
    -- Update application
    UPDATE applications 
    SET application_status = 'Rejected',
        rejection_reason = p_rejection_reason,
        reviewed_by = p_rejected_by,
        reviewed_at = CURRENT_TIMESTAMP
    WHERE application_id = p_application_id;
    
    -- Log the rejection
    INSERT INTO audit_log (
        user_id, action_type, table_affected, record_id,
        new_values, action_description
    ) VALUES (
        p_rejected_by, 'REJECT', 'applications', p_application_id,
        JSON_OBJECT('application_status', 'Rejected', 'rejection_reason', p_rejection_reason),
        CONCAT('Application rejected: ', p_rejection_reason)
    );
    
    SELECT 'Application rejected successfully' AS result;
    
END //

DELIMITER ;


-- ============================================================================
-- Procedure: sp_process_disbursement
-- Purpose: Creates disbursement record for approved application
-- Parameters: p_application_id, p_disbursed_by, p_amount, p_mode
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_process_disbursement;

DELIMITER //

CREATE PROCEDURE sp_process_disbursement(
    IN p_application_id INT,
    IN p_disbursed_by INT,
    IN p_amount DECIMAL(12,2),
    IN p_mode VARCHAR(30)
)
BEGIN
    DECLARE v_beneficiary_id INT;
    DECLARE v_bank_account VARCHAR(20);
    DECLARE v_ifsc VARCHAR(11);
    DECLARE v_approved_amount DECIMAL(12,2);
    DECLARE v_disbursement_number VARCHAR(30);
    DECLARE v_installment_num INT;
    DECLARE v_total_installments INT;
    
    -- Get beneficiary and application details
    SELECT 
        a.beneficiary_id, b.bank_account_number, b.ifsc_code,
        a.approved_amount
    INTO 
        v_beneficiary_id, v_bank_account, v_ifsc, v_approved_amount
    FROM applications a
    INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
    WHERE a.application_id = p_application_id;
    
    -- Validate application is approved
    IF NOT EXISTS (
        SELECT 1 FROM applications 
        WHERE application_id = p_application_id 
        AND application_status = 'Approved'
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Application must be approved before disbursement';
    END IF;
    
    -- Count existing disbursements for this application
    SELECT COALESCE(MAX(installment_number), 0) + 1,
           COALESCE(MAX(total_installments), 1)
    INTO v_installment_num, v_total_installments
    FROM disbursement
    WHERE application_id = p_application_id;
    
    -- Generate disbursement number
    SET v_disbursement_number = CONCAT(
        'DISB-', YEAR(CURDATE()), '-',
        LPAD((SELECT COALESCE(MAX(CAST(SUBSTRING(disbursement_number, 10) AS UNSIGNED)), 0) + 1 
              FROM disbursement), 6, '0')
    );
    
    -- Create disbursement record
    INSERT INTO disbursement (
        application_id, beneficiary_id, disbursement_number,
        disbursement_date, disbursement_amount, disbursement_mode,
        disbursement_status, bank_account_number, ifsc_code,
        authorized_by, authorized_at, installment_number, total_installments
    ) VALUES (
        p_application_id, v_beneficiary_id, v_disbursement_number,
        CURDATE(), p_amount, p_mode,
        'Processing', v_bank_account, v_ifsc,
        p_disbursed_by, CURRENT_TIMESTAMP, v_installment_num, v_total_installments
    );
    
    -- Update application status to Disbursed
    UPDATE applications 
    SET application_status = 'Disbursed'
    WHERE application_id = p_application_id;
    
    -- Log the disbursement
    INSERT INTO audit_log (
        user_id, action_type, table_affected, record_id,
        new_values, action_description
    ) VALUES (
        p_disbursed_by, 'DISBURSE', 'disbursement', LAST_INSERT_ID(),
        JSON_OBJECT(
            'disbursement_number', v_disbursement_number,
            'amount', p_amount,
            'mode', p_mode
        ),
        CONCAT('Disbursement initiated: ', v_disbursement_number, ' for Rs ', p_amount)
    );
    
    SELECT 
        v_disbursement_number AS disbursement_number,
        p_amount AS amount,
        'Processing' AS status;
    
END //

DELIMITER ;


-- ============================================================================
-- SECTION 7: USEFUL QUERIES FOR ELIGIBILITY ANALYSIS
-- ============================================================================

-- Query: Get eligibility distribution by status
-- Purpose: Shows count of applications by eligibility status
-- Usage: Dashboard reporting

SELECT 
    eligibility_status,
    COUNT(*) AS application_count,
    ROUND(AVG(total_eligibility_score), 2) AS average_score,
    MIN(total_eligibility_score) AS min_score,
    MAX(total_eligibility_score) AS max_score
FROM eligibility
GROUP BY eligibility_status
ORDER BY 
    FIELD(eligibility_status, 'Eligible', 'Partially Eligible', 'Not Eligible', 'Pending Review');


-- Query: Get top scoring beneficiaries for a scheme
-- Purpose: Identifies highest-priority beneficiaries
-- Usage: Prioritized processing

SELECT 
    a.application_number,
    CONCAT(b.first_name, ' ', b.last_name) AS beneficiary_name,
    b.category,
    b.annual_income,
    e.total_eligibility_score,
    e.eligibility_status,
    e.evaluation_date
FROM eligibility e
INNER JOIN applications a ON e.application_id = a.application_id
INNER JOIN beneficiaries b ON a.beneficiary_id = b.beneficiary_id
WHERE a.scheme_id = 1  -- PM-KISAN
AND e.eligibility_status = 'Eligible'
ORDER BY e.total_eligibility_score DESC
LIMIT 5;


-- Query: Get scoring component averages
-- Purpose: Analyzes which scoring components contribute most
-- Usage: Scheme optimization

SELECT 
    s.scheme_name,
    COUNT(e.eligibility_id) AS total_evaluated,
    ROUND(AVG(e.age_score), 2) AS avg_age_score,
    ROUND(AVG(e.income_score), 2) AS avg_income_score,
    ROUND(AVG(e.category_score), 2) AS avg_category_score,
    ROUND(AVG(e.disability_score), 2) AS avg_disability_score,
    ROUND(AVG(e.bpl_score), 2) AS avg_bpl_score,
    ROUND(AVG(e.location_score), 2) AS avg_location_score,
    ROUND(AVG(e.total_eligibility_score), 2) AS avg_total_score
FROM eligibility e
INNER JOIN applications a ON e.application_id = a.application_id
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
GROUP BY s.scheme_name
ORDER BY avg_total_score DESC;


-- Query: Get rejection analysis
-- Purpose: Analyzes why applications are rejected
-- Usage: Process improvement

SELECT 
    s.scheme_name,
    COUNT(a.application_id) AS total_rejected,
    GROUP_CONCAT(DISTINCT a.rejection_reason SEPARATOR '; ') AS rejection_reasons
FROM applications a
INNER JOIN schemes s ON a.scheme_id = s.scheme_id
WHERE a.application_status = 'Rejected'
GROUP BY s.scheme_name;


-- ============================================================================
-- SECTION 8: ELIGIBILITY SCORING ENGINE COMPLETE
-- ============================================================================

SELECT 'Eligibility Scoring Engine created successfully!' AS status;
SELECT 
    'Functions Created' AS component,
    7 AS count
UNION ALL
SELECT 
    'Procedures Created',
    5
UNION ALL
SELECT 
    'Config Records',
    (SELECT COUNT(*) FROM eligibility_scoring_config);
