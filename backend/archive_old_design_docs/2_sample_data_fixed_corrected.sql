-- ============================================================================
-- ⚠ LEGACY / NOT USED BY THE RUNNING APPLICATION ⚠
-- Inserts into 1_database_schema_final.sql's table names (beneficiaries,
-- schemes, verification, etc.), which the app's entities don't read from.
-- Running this will NOT make data appear in the app. Kept for reference only.
-- ============================================================================

-- ============================================================================
-- GOVERNMENT BENEFICIARY DISBURSEMENT SYSTEM
-- Sample Data Insertion
--
-- Description: This script inserts realistic sample data into all tables
--              of the Government Beneficiary Disbursement System. Each table
--              contains minimum 5-10 records suitable for testing and demo.
--
-- Compatible With: MySQL 8.0+
-- Author: Database & Eligibility Scoring Module
-- Version: 1.0
-- ============================================================================

USE gov_beneficiary_db;


-- ============================================================================
-- SECTION 1: ROLES DATA
-- ============================================================================
-- Inserting system roles for different user types in the system.

INSERT INTO roles (role_name, role_description, is_active) VALUES
('System Administrator', 'Full access to all system modules and configurations', TRUE),
('State Admin', 'Manages operations at the state level with full state access', TRUE),
('District Officer', 'Oversees district-level beneficiary processing and approvals', TRUE),
('Block Development Officer', 'Manages block-level operations and field verifications', TRUE),
('Village Level Worker', 'Handles ground-level data entry and beneficiary interactions', TRUE),
('Verification Officer', 'Conducts document and field verification for applications', TRUE),
('Disbursement Officer', 'Manages payment processing and disbursement authorizations', TRUE),
('Auditor', 'Read-only access for auditing and compliance monitoring', TRUE),
('Help Desk Operator', 'Handles beneficiary queries and application status updates', TRUE),
('Read Only User', 'View-only access for monitoring and reporting purposes', TRUE);

-- Verify roles inserted
SELECT 'Roles' AS table_name, COUNT(*) AS records_inserted FROM roles;


-- ============================================================================
-- SECTION 2: STATES DATA
-- ============================================================================
-- Inserting Indian states and union territories.

INSERT INTO states (state_code, state_name, is_active) VALUES
('MH', 'Maharashtra', TRUE),
('KA', 'Karnataka', TRUE),
('TN', 'Tamil Nadu', TRUE),
('DL', 'Delhi', TRUE),
('UP', 'Uttar Pradesh', TRUE),
('RJ', 'Rajasthan', TRUE),
('WB', 'West Bengal', TRUE),
('GJ', 'Gujarat', TRUE),
('AP', 'Andhra Pradesh', TRUE),
('KL', 'Kerala', TRUE);

-- Verify states inserted
SELECT 'States' AS table_name, COUNT(*) AS records_inserted FROM states;


-- ============================================================================
-- SECTION 3: DISTRICTS DATA
-- ============================================================================
-- Inserting districts for each state (1-2 per state for sample data).

INSERT INTO districts (state_id, district_code, district_name, is_active) VALUES
(1, 'MUM', 'Mumbai', TRUE),
(1, 'PUN', 'Pune', TRUE),
(2, 'BAN', 'Bangalore Urban', TRUE),
(2, 'MYA', 'Mysore', TRUE),
(3, 'CHE', 'Chennai', TRUE),
(3, 'COI', 'Coimbatore', TRUE),
(4, 'NEW', 'New Delhi', TRUE),
(4, 'NW', 'North West Delhi', TRUE),
(5, 'LKO', 'Lucknow', TRUE),
(5, 'AGC', 'Agra', TRUE),
(6, 'JAI', 'Jaipur', TRUE),
(6, 'JOD', 'Jodhpur', TRUE),
(7, 'KOL', 'Kolkata', TRUE),
(7, 'HWH', 'Howrah', TRUE),
(8, 'AHM', 'Ahmedabad', TRUE),
(8, 'SUR', 'Surat', TRUE),
(9, 'VJW', 'Vijayawada', TRUE),
(9, 'VSK', 'Visakhapatnam', TRUE),
(10, 'TVN', 'Thiruvananthapuram', TRUE),
(10, 'KOC', 'Kochi', TRUE);

-- Verify districts inserted
SELECT 'Districts' AS table_name, COUNT(*) AS records_inserted FROM districts;


-- ============================================================================
-- SECTION 4: BLOCKS DATA
-- ============================================================================
-- Inserting blocks/talukas for each district.

INSERT INTO blocks (district_id, block_code, block_name, is_active) VALUES
(1, 'MUMN', 'Mumbai North Block', TRUE),
(1, 'MUMS', 'Mumbai South Block', TRUE),
(2, 'PUNE', 'Pune East Block', TRUE),
(2, 'PUNW', 'Pune West Block', TRUE),
(3, 'BANC', 'Bangalore Central Block', TRUE),
(3, 'BANN', 'Bangalore North Block', TRUE),
(4, 'MYA1', 'Mysore Block 1', TRUE),
(4, 'MYA2', 'Mysore Block 2', TRUE),
(5, 'CHEN', 'Chennai North Block', TRUE),
(5, 'CHES', 'Chennai South Block', TRUE),
(6, 'COI1', 'Coimbatore Block 1', TRUE),
(6, 'COI2', 'Coimbatore Block 2', TRUE),
(7, 'NEWC', 'New Delhi Central Block', TRUE),
(7, 'NEWE', 'New Delhi East Block', TRUE),
(8, 'NW01', 'North West Block 1', TRUE),
(8, 'NW02', 'North West Block 2', TRUE),
(9, 'LKOC', 'Lucknow Central Block', TRUE),
(9, 'LKON', 'Lucknow North Block', TRUE),
(10, 'AGC1', 'Agra Block 1', TRUE),
(10, 'AGC2', 'Agra Block 2', TRUE),
(11, 'JAIC', 'Jaipur Central Block', TRUE),
(11, 'JAIS', 'Jaipur South Block', TRUE),
(12, 'JOD1', 'Jodhpur Block 1', TRUE),
(12, 'JOD2', 'Jodhpur Block 2', TRUE),
(13, 'KOLC', 'Kolkata Central Block', TRUE),
(13, 'KOLE', 'Kolkata East Block', TRUE),
(14, 'HWH1', 'Howrah Block 1', TRUE),
(14, 'HWH2', 'Howrah Block 2', TRUE),
(15, 'AHMC', 'Ahmedabad Central Block', TRUE),
(15, 'AHMW', 'Ahmedabad West Block', TRUE),
(16, 'SUR1', 'Surat Block 1', TRUE),
(16, 'SUR2', 'Surat Block 2', TRUE),
(17, 'VJWC', 'Vijayawada Central Block', TRUE),
(17, 'VJWN', 'Vijayawada North Block', TRUE),
(18, 'VSKC', 'Visakhapatnam Central Block', TRUE),
(18, 'VSKS', 'Visakhapatnam South Block', TRUE),
(19, 'TVNC', 'Thiruvananthapuram Central Block', TRUE),
(19, 'TVNN', 'Thiruvananthapuram North Block', TRUE),
(20, 'KOCC', 'Kochi Central Block', TRUE),
(20, 'KOCS', 'Kochi South Block', TRUE);

-- Verify blocks inserted
SELECT 'Blocks' AS table_name, COUNT(*) AS records_inserted FROM blocks;


-- ============================================================================
-- SECTION 5: VILLAGES DATA
-- ============================================================================
-- Inserting villages for each block (sample villages).

INSERT INTO villages (block_id, village_code, village_name, pin_code, population, is_active) VALUES
(1, 'VIL-001', 'Malad Village', '400064', 15000, TRUE),
(1, 'VIL-002', 'Borivali Village', '400066', 12000, TRUE),
(2, 'VIL-003', 'Colaba Village', '400005', 8000, TRUE),
(2, 'VIL-004', 'Grant Road Village', '400007', 10000, TRUE),
(3, 'VIL-005', 'Kothrud Village', '411038', 20000, TRUE),
(3, 'VIL-006', 'Hadapsar Village', '411028', 18000, TRUE),
(4, 'VIL-007', 'Warje Village', '411058', 14000, TRUE),
(4, 'VIL-008', 'Bavdhan Village', '411021', 11000, TRUE),
(5, 'VIL-009', 'HSR Layout Village', '560102', 25000, TRUE),
(5, 'VIL-010', 'Koramangala Village', '560034', 22000, TRUE),
(6, 'VIL-011', 'Hebbal Village', '560024', 16000, TRUE),
(6, 'VIL-012', 'Yelahanka Village', '560063', 19000, TRUE),
(7, 'VIL-013', 'Saraswathipuram', '570009', 13000, TRUE),
(7, 'VIL-014', 'Vani Vilas Mohalla', '570017', 11000, TRUE),
(8, 'VIL-015', 'Jayalakshmipuram', '570012', 9000, TRUE),
(8, 'VIL-016', 'Gokulam Village', '570002', 8000, TRUE),
(9, 'VIL-017', 'T Nagar Village', '600017', 21000, TRUE),
(9, 'VIL-018', 'Mylapore Village', '600004', 18000, TRUE),
(10, 'VIL-019', 'Adyar Village', '600020', 23000, TRUE),
(10, 'VIL-020', 'Velachery Village', '600042', 26000, TRUE),
(11, 'VIL-021', 'Gandhipuram Village', '641012', 15000, TRUE),
(11, 'VIL-022', 'RS Puram Village', '641002', 12000, TRUE),
(12, 'VIL-023', 'Peelamedu Village', '641004', 10000, TRUE),
(12, 'VIL-024', 'Sulur Village', '641402', 8000, TRUE),
(13, 'VIL-025', 'Connaught Place Area', '110001', 30000, TRUE),
(13, 'VIL-026', 'Karol Bagh Village', '110005', 25000, TRUE),
(14, 'VIL-027', 'Lajpat Nagar Village', '110024', 20000, TRUE),
(14, 'VIL-028', 'Defence Colony Area', '110024', 18000, TRUE),
(15, 'VIL-029', 'Dwarka Sector Village', '110075', 22000, TRUE),
(15, 'VIL-030', 'Rohini Village', '110085', 28000, TRUE),
(17, 'VIL-031', 'Gomti Nagar Village', '226010', 35000, TRUE),
(17, 'VIL-032', 'Aliganj Village', '226024', 28000, TRUE),
(18, 'VIL-033', 'Indira Nagar Village', '226016', 24000, TRUE),
(18, 'VIL-034', 'Hazratganj Area', '226001', 20000, TRUE),
(19, 'VIL-035', 'Sadar Bazar Village', '282001', 30000, TRUE),
(19, 'VIL-036', 'Civil Lines Village', '282002', 22000, TRUE),
(20, 'VIL-037', 'Mantola Village', '282003', 18000, TRUE),
(20, 'VIL-038', 'Katra Village', '282004', 15000, TRUE),
(21, 'VIL-039', 'Mansarovar Village', '302020', 40000, TRUE),
(21, 'VIL-040', 'Vaishali Nagar Village', '302021', 35000, TRUE),
(22, 'VIL-041', 'Malviya Nagar Village', '302017', 28000, TRUE),
(22, 'VIL-042', 'Jagatpura Village', '302025', 22000, TRUE),
(23, 'VIL-043', 'Paota Village', '342001', 25000, TRUE),
(23, 'VIL-044', 'Shastri Nagar Village', '342003', 20000, TRUE),
(24, 'VIL-045', 'Sardarpura Village', '342005', 18000, TRUE),
(24, 'VIL-046', 'Ratanada Village', '342006', 15000, TRUE),
(25, 'VIL-047', 'Salt Lake Village', '700064', 45000, TRUE),
(25, 'VIL-048', 'Sector V Village', '700091', 38000, TRUE),
(26, 'VIL-049', 'New Town Area', '700156', 32000, TRUE),
(26, 'VIL-050', 'Rajarhat Village', '700136', 28000, TRUE);

-- Verify villages inserted
SELECT 'Villages' AS table_name, COUNT(*) AS records_inserted FROM villages;


-- ============================================================================
-- SECTION 6: USERS DATA
-- ============================================================================
-- Inserting system users with different roles.

INSERT INTO users (role_id, village_id, first_name, last_name, email, phone_number, username, password_hash, employee_code, is_active, last_login_at) VALUES
(1, NULL, 'Rajesh', 'Kumar', 'rajesh.kumar@gov.in', '9876543210', 'rajesh.admin', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0001', TRUE, '2026-07-15 09:00:00'),
(2, NULL, 'Priya', 'Sharma', 'priya.sharma@gov.in', '9876543211', 'priya.state', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0002', TRUE, '2026-07-15 08:30:00'),
(3, NULL, 'Amit', 'Patel', 'amit.patel@gov.in', '9876543212', 'amit.district', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0003', TRUE, '2026-07-14 10:15:00'),
(4, NULL, 'Sunita', 'Devi', 'sunita.devi@gov.in', '9876543213', 'sunita.bdo', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0004', TRUE, '2026-07-15 07:45:00'),
(5, 1, 'Ramesh', 'Verma', 'ramesh.verma@gov.in', '9876543214', 'ramesh.vlw', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0005', TRUE, '2026-07-15 06:30:00'),
(6, NULL, 'Kavita', 'Singh', 'kavita.singh@gov.in', '9876543215', 'kavita.verifier', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0006', TRUE, '2026-07-15 11:00:00'),
(7, NULL, 'Manoj', 'Tiwari', 'manoj.tiwari@gov.in', '9876543216', 'manoj.disb', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0007', TRUE, '2026-07-15 12:00:00'),
(8, NULL, 'Anjali', 'Gupta', 'anjali.gupta@gov.in', '9876543217', 'anjali.auditor', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0008', TRUE, '2026-07-14 14:30:00'),
(9, NULL, 'Vikram', 'Rao', 'vikram.rao@gov.in', '9876543218', 'vikram.helpdesk', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0009', TRUE, '2026-07-15 13:00:00'),
(10, NULL, 'Neha', 'Joshi', 'neha.joshi@gov.in', '9876543219', 'neha.readonly', '$2b$12$LJ3m4ks8hS1eG9f5a2k3xOqzR8yT0vU1wX2yZ3aB4cD5eF6gH7iJ', 'EMP-0010', TRUE, '2026-07-15 10:00:00');

-- Verify users inserted
SELECT 'Users' AS table_name, COUNT(*) AS records_inserted FROM users;


-- ============================================================================
-- SECTION 7: BENEFICIARIES DATA
-- ============================================================================
-- Inserting sample beneficiary records with diverse demographics.

INSERT INTO beneficiaries (village_id, first_name, last_name, date_of_birth, gender, aadhaar_number, pan_number, phone_number, email, address_line1, address_line2, pin_code, annual_income, category, disability_status, is_bpl, bank_account_number, ifsc_code, is_active) VALUES
(1, 'Suresh', 'Yadav', '1985-03-15', 'Male', '123456789012', 'ABCPY1234D', '9123456780', 'suresh.yadav@email.com', '12, MG Road, Malad', 'Near Bus Stand', '400064', 75000.00, 'OBC', FALSE, TRUE, '50100123456789', 'HDFC0001234', TRUE),
(3, 'Lakshmi', 'Nair', '1990-07-22', 'Female', '234567890123', NULL, '9123456781', 'lakshmi.nair@email.com', '45, Anna Salai, Colaba', 'Flat 2B', '400005', 45000.00, 'SC', FALSE, TRUE, '50100234567890', 'SBIN0002345', TRUE),
(5, 'Ravi', 'Shankar', '1978-11-08', 'Male', '345678901234', 'BCDRS4567E', '9123456782', 'ravi.shankar@email.com', '78, FC Road, Kothrud', NULL, '411038', 120000.00, 'General', FALSE, FALSE, '50100345678901', 'ICIC0003456', TRUE),
(9, 'Meena', 'Kumari', '1995-02-14', 'Female', '456789012345', 'DEFMK7890F', '9123456783', 'meena.kumari@email.com', '23, 5th Block, HSR Layout', 'Near Park', '560102', 35000.00, 'ST', TRUE, TRUE, '50100456789012', 'UBIN0004567', TRUE),
(17, 'Arun', 'Prasad', '1982-06-30', 'Male', '567890123456', 'GHIAP0123G', '9123456784', 'arun.prasad@email.com', '90, T Nagar Main Road', '1st Floor', '600017', 95000.00, 'OBC', FALSE, FALSE, '50100567890123', 'CBIN0005678', TRUE),
(25, 'Priyanka', 'Das', '1988-09-18', 'Female', '678901234565', 'JKLPD6789H', '9123456785', 'priyanka.das@email.com', '34, CP Area, New Delhi', NULL, '110001', 28000.00, 'SC', TRUE, TRUE, '50100678901234', 'PUNB0006789', TRUE),
(31, 'Vikash', 'Srivastava', '1975-04-25', 'Male', '789012345678', 'LMNVS1234I', '9123456786', 'vikash.sri@email.com', '56, Gomti Nagar', 'Behind Temple', '226010', 18000.00, 'General', TRUE, TRUE, '50100789012345', 'UBIN0007890', TRUE),
(39, 'Sunita', 'Bai', '1992-12-01', 'Female', '890123456789', NULL, '9123456787', 'sunita.bai@email.com', '12, Mansarovar Extension', 'Lane 3', '302020', 22000.00, 'ST', FALSE, TRUE, '50100890123456', 'BARB0008901', TRUE),
(47, 'Anil', 'Ghosh', '1980-08-12', 'Male', '901234567890', 'NOPAG4567J', '9123456788', 'anil.ghosh@email.com', '78, Salt Lake Sector 1', '3rd Floor', '700064', 150000.00, 'EWS', FALSE, FALSE, '50100901234567', 'UCBA0009012', TRUE),
(13, 'Geeta', 'Devi', '1998-01-20', 'Female', '012345678901', 'PQRGD8901K', '9123456789', 'geeta.devi@email.com', '45, Saraswathipuram', NULL, '570009', 32000.00, 'SC', TRUE, TRUE, '50101012345678', 'SYNB0010123', TRUE),
(21, 'Ramesh', 'Choudhary', '1970-05-05', 'Male', '112233445566', 'RCCRC1234L', '9123456790', 'ramesh.ch@email.com', '67, Mansarovar Main', 'Block C', '302020', 48000.00, 'OBC', FALSE, TRUE, '50101123456789', 'HDFC0011234', TRUE),
(27, 'Fatima', 'Begum', '1987-10-10', 'Female', '223344556677', NULL, '9123456791', 'fatima.begum@email.com', '23, Lajpat Nagar', 'Ground Floor', '110024', 42000.00, 'General', TRUE, FALSE, '50101234567890', 'ICIC0012345', TRUE);

-- Verify beneficiaries inserted
SELECT 'Beneficiaries' AS table_name, COUNT(*) AS records_inserted FROM beneficiaries;


-- ============================================================================
-- SECTION 8: SCHEMES DATA
-- ============================================================================
-- Inserting government welfare scheme definitions with eligibility criteria.

INSERT INTO schemes (scheme_code, scheme_name, scheme_description, scheme_category, min_age, max_age, min_annual_income, max_annual_income, eligible_categories, requires_disability, requires_bpl, max_disbursement_amount, installment_count, is_active, effective_from, effective_to) VALUES
('PM-KISAN', 'PM-KISAN Samman Nidhi', 'Direct income support of Rs 6000 per year to farmer families having cultivable land holding', 'Agriculture', 18, 65, NULL, 300000.00, 'General,SC,ST,OBC,EWS', FALSE, FALSE, 6000.00, 3, TRUE, '2019-02-01', NULL),
('PMAY-G', 'Pradhan Mantri Awas Yojana - Gramin', 'Financial assistance for construction of pucca houses for eligible rural households', 'Housing', 21, 55, NULL, 200000.00, 'SC,ST,OBC,EWS', FALSE, TRUE, 120000.00, 1, TRUE, '2016-04-01', NULL),
('NSP-SC', 'National Scholarship for SC Students', 'Pre-matric and post-matric scholarship for Scheduled Caste students', 'Education', 5, 25, NULL, 250000.00, 'SC', FALSE, FALSE, 50000.00, 1, TRUE, '2015-06-01', NULL),
('DISABILITY-ADP', 'ADIP Scheme for Disabled Persons', 'Assistance to disabled persons for purchase/fitting of aids and appliances', 'Social Welfare', 5, 65, NULL, 150000.00, 'General,SC,ST,OBC,EWS', TRUE, FALSE, 30000.00, 1, TRUE, '2017-04-01', NULL),
('MGNREGA', 'Mahatma Gandhi NREGA', 'Guaranteed 100 days of wage employment per year to rural households', 'Employment', 18, 60, NULL, 120000.00, 'General,SC,ST,OBC,EWS', FALSE, TRUE, 25000.00, 10, TRUE, '2005-02-02', NULL),
('PM-JJY', 'Pradhan Mantri Jeevan Jyoti Bima Yojana', 'Life insurance cover of Rs 2 lakh at minimal premium for eligible citizens', 'Social Welfare', 18, 50, NULL, 500000.00, 'General,SC,ST,OBC,EWS', FALSE, FALSE, 200000.00, 1, TRUE, '2015-05-09', NULL),
('PM-UY', 'PM Ujjwala Yojana', 'Free LPG connections to women from BPL households', 'Subsidy', 18, 60, NULL, 100000.00, 'SC,ST,OBC,EWS', FALSE, TRUE, 1600.00, 1, TRUE, '2016-05-01', NULL),
('SSP-KARNATAKA', 'State Scholarship Portal Karnataka', 'Scholarship for students from economically weaker sections in Karnataka', 'Education', 6, 22, NULL, 200000.00, 'SC,ST,OBC,EWS', FALSE, FALSE, 30000.00, 1, TRUE, '2020-07-01', NULL),
('PM-KMY', 'Pradhan Mantri Kisan Maandhan Yojana', 'Pension scheme for small and marginal farmers with voluntary enrollment', 'Pension', 18, 40, NULL, 150000.00, 'General,SC,ST,OBC,EWS', FALSE, FALSE, 36000.00, 12, TRUE, '2019-08-09', NULL),
('WB-LAKSHMI', 'Lakshmi Bhandar Scheme West Bengal', 'Monthly basic income support to women heads of households in West Bengal', 'Social Welfare', 18, 60, NULL, 120000.00, 'SC,ST,OBC,General', FALSE, TRUE, 12000.00, 12, TRUE, '2023-09-01', NULL);

-- Verify schemes inserted
SELECT 'Schemes' AS table_name, COUNT(*) AS records_inserted FROM schemes;


-- ============================================================================
-- SECTION 9: APPLICATIONS DATA
-- ============================================================================
-- Inserting sample applications for various beneficiaries and schemes.

INSERT INTO applications (beneficiary_id, scheme_id, application_number, application_date, application_status, applied_amount, approved_amount, rejection_reason, reviewed_by, reviewed_at, remarks) VALUES
(1, 1, 'APP-2026-000001', '2026-01-15', 'Approved', 6000.00, 6000.00, NULL, 3, '2026-01-20 10:00:00', 'Farmer eligibility confirmed'),
(2, 3, 'APP-2026-000002', '2026-01-20', 'Disbursed', 45000.00, 45000.00, NULL, 3, '2026-01-25 11:00:00', 'SC student scholarship approved'),
(3, 9, 'APP-2026-000003', '2026-02-01', 'Rejected', 36000.00, NULL, 'Income exceeds eligibility threshold', 3, '2026-02-05 09:30:00', 'Annual income above Rs 1.5 lakh'),
(4, 5, 'APP-2026-000004', '2026-02-10', 'Approved', 25000.00, 25000.00, NULL, 4, '2026-02-15 14:00:00', 'ST candidate eligible for MGNREGA'),
(5, 6, 'APP-2026-000005', '2026-02-15', 'Verified', 200000.00, NULL, NULL, NULL, NULL, 'Application under verification'),
(6, 4, 'APP-2026-000006', '2026-03-01', 'Eligibility Checked', 30000.00, NULL, NULL, NULL, NULL, 'Eligibility evaluation pending'),
(7, 5, 'APP-2026-000007', '2026-03-10', 'Approved', 25000.00, 22000.00, NULL, 4, '2026-03-15 10:00:00', 'Disability verified, amount adjusted'),
(8, 2, 'APP-2026-000008', '2026-03-20', 'Submitted', 120000.00, NULL, NULL, NULL, NULL, 'Application received'),
(9, 8, 'APP-2026-000009', '2026-04-01', 'Disbursed', 25000.00, 25000.00, NULL, 3, '2026-04-05 11:00:00', 'Scholarship disbursed to bank account'),
(10, 7, 'APP-2026-000010', '2026-04-10', 'Approved', 1600.00, 1600.00, NULL, 3, '2026-04-12 09:00:00', 'Ujjwala connection approved'),
(11, 10, 'APP-2026-000011', '2026-04-20', 'Submitted', 12000.00, NULL, NULL, NULL, NULL, 'Under review'),
(12, 4, 'APP-2026-000012', '2026-05-01', 'Under Review', 30000.00, NULL, NULL, NULL, NULL, 'Documents under scrutiny');

-- Verify applications inserted
SELECT 'Applications' AS table_name, COUNT(*) AS records_inserted FROM applications;


-- ============================================================================
-- SECTION 10: ELIGIBILITY DATA
-- ============================================================================
-- Inserting eligibility evaluation results for processed applications.

INSERT INTO eligibility (application_id, evaluated_by, evaluation_date, age_score, income_score, category_score, disability_score, bpl_score, location_score, total_eligibility_score, max_possible_score, eligibility_status, evaluation_notes) VALUES
(1, 6, '2026-01-17 09:00:00', 18, 20, 15, 0, 20, 15, 88, 100, 'Eligible', 'Farmer with valid land records, income well within limit'),
(2, 6, '2026-01-22 10:00:00', 20, 18, 20, 0, 15, 12, 85, 100, 'Eligible', 'SC student from BPL family, strong academic record'),
(3, 6, '2026-02-02 11:00:00', 15, 5, 10, 0, 0, 10, 40, 100, 'Not Eligible', 'Income exceeds scheme threshold by significant margin'),
(4, 6, '2026-02-12 09:30:00', 20, 20, 18, 5, 20, 12, 95, 100, 'Eligible', 'ST candidate, disabled, BPL card holder - highest priority'),
(5, 6, '2026-02-18 14:00:00', 18, 15, 12, 0, 10, 10, 65, 100, 'Partially Eligible', 'Meets basic criteria, income borderline'),
(6, 6, '2026-03-05 10:00:00', 15, 20, 0, 20, 15, 12, 82, 100, 'Eligible', 'Disability verified, low income qualifies'),
(7, 6, '2026-03-12 11:00:00', 20, 20, 10, 20, 20, 10, 100, 100, 'Eligible', 'Maximum eligibility score - all criteria met'),
(8, 6, '2026-03-25 09:00:00', 12, 10, 10, 0, 15, 8, 55, 100, 'Partially Eligible', 'Housing scheme requires BPL verification pending'),
(9, 6, '2026-04-02 10:30:00', 20, 18, 15, 0, 18, 12, 83, 100, 'Eligible', 'Karnataka state scholarship criteria fully met'),
(10, 6, '2026-04-11 08:30:00', 18, 20, 12, 0, 20, 10, 80, 100, 'Eligible', 'BPL household qualifies for Ujjwala scheme'),
(11, 6, '2026-04-22 09:00:00', 18, 20, 15, 0, 20, 15, 88, 100, 'Eligible', 'West Bengal resident, BPL card holder, low income'),
(12, 6, '2026-05-05 10:00:00', 15, 18, 0, 20, 15, 10, 78, 100, 'Eligible', 'Disability confirmed, income within limits');

-- Verify eligibility inserted
SELECT 'Eligibility' AS table_name, COUNT(*) AS records_inserted FROM eligibility;


-- ============================================================================
-- SECTION 11: VERIFICATION DATA
-- ============================================================================
-- Inserting verification records for applications.

INSERT INTO verification (application_id, verifier_id, verification_type, verification_status, verification_date, document_verified, field_visit_done, aadhaar_verified, income_verified, address_verified, verification_notes, supporting_documents, created_at) VALUES
(1, 6, 'Aadhaar Authentication', 'Passed', '2026-01-16 10:00:00', TRUE, FALSE, TRUE, TRUE, FALSE, 'Aadhaar verified successfully, income certificate confirmed', 'income_cert_001.pdf', '2026-01-16 09:00:00'),
(2, 6, 'Document', 'Passed', '2026-01-21 11:00:00', TRUE, FALSE, TRUE, TRUE, FALSE, 'Caste certificate and income certificate verified', 'caste_cert_002.pdf, income_cert_002.pdf', '2026-01-21 10:00:00'),
(3, 6, 'Income Verification', 'Failed', '2026-02-03 09:00:00', TRUE, FALSE, TRUE, FALSE, FALSE, 'Income exceeds Rs 1.5 lakh limit, application rejected', 'income_cert_003.pdf', '2026-02-03 08:00:00'),
(4, 6, 'Field Visit', 'Passed', '2026-02-13 14:00:00', TRUE, TRUE, TRUE, TRUE, TRUE, 'Field visit confirmed BPL status and disability', 'field_report_004.pdf', '2026-02-13 13:00:00'),
(5, 6, 'Document', 'In Progress', NULL, FALSE, FALSE, FALSE, FALSE, FALSE, 'Waiting for income certificate submission', NULL, '2026-03-05 10:00:00'),
(6, 6, 'Aadhaar Authentication', 'Passed', '2026-03-04 10:00:00', TRUE, FALSE, TRUE, FALSE, FALSE, 'Aadhaar verified, disability certificate under review', 'aadhaar_006.xml', '2026-03-04 09:00:00'),
(7, 6, 'Field Visit', 'Passed', '2026-03-11 11:00:00', TRUE, TRUE, TRUE, TRUE, TRUE, 'Disability verified by medical board, all documents clear', 'field_report_007.pdf, disability_cert_007.pdf', '2026-03-11 10:00:00'),
(8, 6, 'Document', 'Pending', NULL, FALSE, FALSE, FALSE, FALSE, FALSE, 'Documents not yet submitted by applicant', NULL, '2026-03-25 09:00:00'),
(9, 6, 'Document', 'Passed', '2026-04-03 10:00:00', TRUE, FALSE, TRUE, TRUE, FALSE, 'Student enrollment verified, income certificate valid', 'enrollment_009.pdf', '2026-04-03 09:00:00'),
(10, 6, 'Aadhaar Authentication', 'Passed', '2026-04-11 09:00:00', TRUE, FALSE, TRUE, TRUE, FALSE, 'Aadhaar and BPL card verified successfully', 'aadhaar_010.xml', '2026-04-11 08:00:00'),
(11, 6, 'Field Visit', 'In Progress', NULL, FALSE, FALSE, FALSE, FALSE, FALSE, 'Field visit scheduled for next week', NULL, '2026-04-22 09:00:00'),
(12, 6, 'Document', 'Pending', NULL, FALSE, FALSE, FALSE, FALSE, FALSE, 'Disability certificate verification in progress', NULL, '2026-05-05 10:00:00');

-- Verify verification inserted
SELECT 'Verification' AS table_name, COUNT(*) AS records_inserted FROM verification;


-- ============================================================================
-- SECTION 12: DISBURSEMENT DATA
-- ============================================================================
-- Inserting disbursement records for approved applications.

INSERT INTO disbursement (application_id, beneficiary_id, disbursement_number, disbursement_date, disbursement_amount, disbursement_mode, disbursement_status, bank_account_number, ifsc_code, transaction_reference, authorized_by, authorized_at, failure_reason, installment_number, total_installments) VALUES
(1, 1, 'DISB-2026-000001', '2026-01-25', 2000.00, 'Direct Bank Transfer', 'Completed', '50100123456789', 'HDFC0001234', 'TXN-HDFC-20260125-001', 7, '2026-01-24 16:00:00', NULL, 1, 3),
(2, 2, 'DISB-2026-000002', '2026-02-01', 45000.00, 'DBT', 'Completed', '50100234567890', 'SBIN0002345', 'TXN-SBI-20260201-001', 7, '2026-01-31 15:00:00', NULL, 1, 1),
(4, 4, 'DISB-2026-000003', '2026-02-20', 5000.00, 'Direct Bank Transfer', 'Completed', '50100456789012', 'UBIN0004567', 'TXN-UBI-20260220-001', 7, '2026-02-19 14:00:00', NULL, 1, 5),
(4, 4, 'DISB-2026-000004', '2026-03-20', 5000.00, 'Direct Bank Transfer', 'Completed', '50100456789012', 'UBIN0004567', 'TXN-UBI-20260320-001', 7, '2026-03-19 14:00:00', NULL, 2, 5),
(7, 7, 'DISB-2026-000005', '2026-03-20', 22000.00, 'Direct Bank Transfer', 'Completed', '50100789012345', 'UBIN0007890', 'TXN-UBI-20260320-002', 7, '2026-03-19 16:00:00', NULL, 1, 1),
(9, 9, 'DISB-2026-000006', '2026-04-08', 25000.00, 'DBT', 'Completed', '50101012345678', 'SYNB0010123', 'TXN-SYN-20260408-001', 7, '2026-04-07 15:00:00', NULL, 1, 1),
(10, 10, 'DISB-2026-000007', '2026-04-15', 1600.00, 'Direct Bank Transfer', 'Processing', '50101012345678', 'SYNB0010123', NULL, 7, '2026-04-14 16:00:00', NULL, 1, 1),
(1, 1, 'DISB-2026-000008', '2026-04-25', 2000.00, 'Direct Bank Transfer', 'Completed', '50100123456789', 'HDFC0001234', 'TXN-HDFC-20260425-001', 7, '2026-04-24 16:00:00', NULL, 2, 3),
(1, 1, 'DISB-2026-000009', '2026-05-25', 2000.00, 'Direct Bank Transfer', 'Pending', '50100123456789', 'HDFC0001234', NULL, NULL, NULL, NULL, 3, 3),
(4, 4, 'DISB-2026-000010', '2026-04-20', 5000.00, 'Direct Bank Transfer', 'Failed', '50100456789012', 'UBIN0004567', NULL, 7, '2026-04-19 14:00:00', 'Bank account temporarily frozen', 3, 5);

-- Verify disbursement inserted
SELECT 'Disbursement' AS table_name, COUNT(*) AS records_inserted FROM disbursement;


-- ============================================================================
-- SECTION 13: AUDIT LOG DATA
-- ============================================================================
-- Inserting sample audit log entries (beyond trigger-generated ones).

INSERT INTO audit_log (user_id, action_type, table_affected, record_id, old_values, new_values, ip_address, user_agent, action_description, action_timestamp) VALUES
(1, 'LOGIN', 'users', 1, NULL, '{"user_id": 1, "username": "rajesh.admin"}', '192.168.1.100', 'Mozilla/5.0 Windows', 'System administrator logged in', '2026-07-15 09:00:00'),
(1, 'UPDATE', 'schemes', 1, '{"is_active": false}', '{"is_active": true}', '192.168.1.100', 'Mozilla/5.0 Windows', 'PM-KISAN scheme reactivated', '2026-07-15 09:15:00'),
(2, 'INSERT', 'users', 11, NULL, '{"user_id": 11, "role_id": 5}', '192.168.1.101', 'Mozilla/5.0 Windows', 'New VLW user created by state admin', '2026-07-15 10:00:00'),
(3, 'APPROVE', 'applications', 4, '{"application_status": "Verified"}', '{"application_status": "Approved", "approved_amount": 25000}', '192.168.1.102', 'Mozilla/5.0 Windows', 'Application APP-2026-000004 approved by district officer', '2026-02-15 14:00:00'),
(6, 'VERIFY', 'verification', 4, '{"verification_status": "Pending"}', '{"verification_status": "Passed", "field_visit_done": true}', '192.168.1.105', 'Mozilla/5.0 Windows', 'Field visit completed and verified for application APP-2026-000004', '2026-02-13 14:00:00'),
(7, 'DISBURSE', 'disbursement', 1, '{"disbursement_status": "Pending"}', '{"disbursement_status": "Completed", "transaction_reference": "TXN-HDFC-20260125-001"}', '192.168.1.106', 'Mozilla/5.0 Windows', 'First installment disbursed for PM-KISAN', '2026-01-25 10:00:00'),
(3, 'REJECT', 'applications', 3, '{"application_status": "Eligibility Checked"}', '{"application_status": "Rejected", "rejection_reason": "Income exceeds eligibility threshold"}', '192.168.1.102', 'Mozilla/5.0 Windows', 'Application APP-2026-000003 rejected - income criterion not met', '2026-02-05 09:30:00'),
(8, 'SELECT', 'eligibility', NULL, NULL, NULL, '192.168.1.107', 'Mozilla/5.0 Windows', 'Auditor accessed eligibility report', '2026-07-14 14:30:00'),
(1, 'LOGOUT', 'users', 1, '{"last_login_at": "2026-07-15 09:00:00"}', NULL, '192.168.1.100', 'Mozilla/5.0 Windows', 'System administrator logged out', '2026-07-15 17:00:00'),
(4, 'UPDATE', 'applications', 7, '{"approved_amount": null}', '{"approved_amount": 22000}', '192.168.1.103', 'Mozilla/5.0 Windows', 'Application amount adjusted based on disability verification', '2026-03-15 10:00:00');

-- Verify audit logs inserted
SELECT 'Audit Log' AS table_name, COUNT(*) AS records_inserted FROM audit_log;


-- ============================================================================
-- SECTION 14: DATA VERIFICATION QUERIES
-- ============================================================================
-- Quick verification that all data was inserted correctly.

SELECT '========== DATA INSERTION SUMMARY ==========' AS section;

SELECT 'roles' AS table_name, COUNT(*) AS total_records FROM roles
UNION ALL
SELECT 'states', COUNT(*) FROM states
UNION ALL
SELECT 'districts', COUNT(*) FROM districts
UNION ALL
SELECT 'blocks', COUNT(*) FROM blocks
UNION ALL
SELECT 'villages', COUNT(*) FROM villages
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'beneficiaries', COUNT(*) FROM beneficiaries
UNION ALL
SELECT 'schemes', COUNT(*) FROM schemes
UNION ALL
SELECT 'applications', COUNT(*) FROM applications
UNION ALL
SELECT 'eligibility', COUNT(*) FROM eligibility
UNION ALL
SELECT 'verification', COUNT(*) FROM verification
UNION ALL
SELECT 'disbursement', COUNT(*) FROM disbursement
UNION ALL
SELECT 'audit_log', COUNT(*) FROM audit_log;

SELECT '========== SAMPLE DATA INSERTION COMPLETE ==========' AS status;