# Business Rules

## Overview

Business Rules define the operational policies that govern the Government Grant Disbursement System.


# General Rules

## User Registration

- Every beneficiary must register before applying.
- Email and mobile number must be unique.


## Authentication

- Users must log in using valid credentials.
- Unauthorized users cannot access protected modules.


## Scheme Rules

- A beneficiary may apply only for schemes for which they are eligible.
- Closed or expired schemes cannot accept new applications.

## Application Rules

- Required fields cannot be left empty.
- Mandatory documents must be uploaded.
- Applications can be edited only before verification begins.

## Verification Rules

- Field Officer performs document verification.
- District Officer reviews the verification report.
- Missing documents require beneficiary resubmission.


## Approval Rules

- Only verified applications can be approved.
- Finance Officer checks budget availability before approval.

## Disbursement Rules

- Funds are transferred only after approval.
- Bank account verification is mandatory.
- Every transaction must generate a transaction record.


## Audit Rules

The system records:

- Login Activities
- Application Submission
- Verification Status
- Approval Status
- Fund Disbursement
- User Actions

## Notification Rules

The system notifies beneficiaries whenever:

- Application Submitted
- Application Rejected
- Documents Required
- Application Approved
- Funds Released

## Security Rules

- Passwords must be encrypted.
- Role-Based Access Control (RBAC) must be implemented.
- Every transaction must be logged.