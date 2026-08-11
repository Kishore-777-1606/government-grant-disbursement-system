USE government_subsidy_db;

-- ----------------------------------------------------------------------------
-- Beneficiaries
-- ----------------------------------------------------------------------------
INSERT INTO beneficiary
    (beneficiary_uid, first_name, last_name, date_of_birth, gender, mobile_number, email,
     address_line1, address_line2, village_id, block_id, district_id, state_id, pincode,
     bank_account_number, ifsc_code, bank_name, category, annual_income, disability_status,
     aadhaar_verified, bank_verified, is_active)
VALUES
    ('BEN-0001', 'Ramesh', 'Kumar', '1985-04-12', 'Male', '9876543210', 'ramesh.kumar@example.com',
     'House No 12, Gandhi Nagar', NULL, 1, 1, 1, 1, '440001',
     '11112222333', 'SBIN0001234', 'State Bank of India', 'OBC', 180000.00, FALSE,
     TRUE, TRUE, TRUE),

    ('BEN-0002', 'Sunita', 'Devi', '1990-09-23', 'Female', '9876501234', 'sunita.devi@example.com',
     'Village Rampur', NULL, 2, 1, 1, 1, '440002',
     '22223333444', 'PUNB0002345', 'Punjab National Bank', 'SC', 95000.00, FALSE,
     TRUE, TRUE, TRUE),

    ('BEN-0003', 'Arjun', 'Verma', '1978-01-05', 'Male', '9012345678', 'arjun.verma@example.com',
     'Sector 9, New Colony', NULL, 3, 2, 1, 1, '440003',
     '33334444555', 'HDFC0003456', 'HDFC Bank', 'General', 320000.00, TRUE,
     TRUE, FALSE, TRUE),

    ('BEN-0004', 'Fatima', 'Sheikh', '1995-06-30', 'Female', '9988776655', 'fatima.sheikh@example.com',
     'Near Bus Stand, Old Town', NULL, 4, 2, 2, 1, '440010',
     '44445555666', 'ICIC0004567', 'ICICI Bank', 'EWS', 110000.00, FALSE,
     FALSE, TRUE, TRUE);


-- ----------------------------------------------------------------------------
-- Schemes
-- ----------------------------------------------------------------------------
INSERT INTO scheme
    (scheme_code, name, description, scheme_type, disbursement_mode, frequency,
     amount, max_annual_income, allowed_categories, max_beneficiaries, start_date, end_date, is_active)
VALUES
    ('SCH-EDU-01', 'Rural Education Support Grant', 'Grant supporting school fees for rural households',
     'Education', 'Bank Transfer', 'One-Time', 25000.00, 250000.00, 'SC,ST,OBC,EWS', 500,
     '2025-04-01', '2027-03-31', TRUE),

    ('SCH-AGR-02', 'Small Farmer Input Subsidy', 'Subsidy for seeds/fertilizer for small landholders',
     'Agriculture', 'Bank Transfer', 'Annual', 40000.00, 300000.00, NULL, 1000,
     '2025-06-01', '2027-05-31', TRUE),

    ('SCH-DIS-03', 'Disability Support Allowance', 'Monthly support allowance for beneficiaries with disability',
     'Welfare', 'Bank Transfer', 'One-Time', 60000.00, 400000.00, NULL, 200,
     '2025-01-01', NULL, TRUE);


-- ----------------------------------------------------------------------------
-- Applications
-- ----------------------------------------------------------------------------
INSERT INTO applications
    (assigned_officer, beneficiary_id, scheme_id, application_date, status, eligibility_score,
     remarks, applied_amount, approved_amount)
VALUES
    (NULL, 1, 1, '2026-01-10', 'Eligible', 80, NULL, 25000.00, NULL),
    (NULL, 2, 1, '2026-01-12', 'Eligible', 70, NULL, 25000.00, NULL),
    (NULL, 3, 3, '2026-01-15', 'Eligible', 100, NULL, 60000.00, NULL),
    (NULL, 4, 2, '2026-01-18', 'Not Eligible', 50, NULL, 40000.00, NULL);


-- ----------------------------------------------------------------------------
-- Verifications (first-stage rows only, for demo purposes)
-- ----------------------------------------------------------------------------
INSERT INTO verifications (application_id, verification_status, verified_by, verification_date, remarks)
VALUES
    (1, 'Pending', 'District Officer', '2026-01-10', 'Waiting for District Officer Verification'),
    (2, 'Pending', 'Field Officer', '2026-01-12', 'Waiting for Field Officer Verification'),
    (3, 'Pending', 'District Officer', '2026-01-15', 'Waiting for District Officer Verification');


-- Note: disbursement_plans / disbursement_installments / compliance_milestones /
-- finance_approvals rows are intentionally left out of this seed file - they are
-- created automatically by the app's own workflow (verification approval ->
-- finance approval -> auto-generated disbursement plan), so hand-seeding them
-- here would just create data the workflow logic didn't actually produce.