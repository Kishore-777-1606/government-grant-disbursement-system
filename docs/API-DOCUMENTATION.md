\# Government Grant Disbursement System

\# Milestone 4 API Documentation



\## Authentication



All protected APIs require a valid JWT token.



Authorization header:



Authorization: Bearer <JWT\_TOKEN>



Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\# 1. Authentication



\## Login



POST /api/auth/login



Required Role:

Public



Request:



{

&#x20; "username": "username",

&#x20; "password": "password"

}



Response:



Returns JWT authentication token and user information.



\---



\# 2. Beneficiary APIs



\## Get All Beneficiaries



GET /beneficiaries



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Response:



Returns list of beneficiaries.



\---



\## Get Beneficiary



GET /beneficiaries/{id}



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Response:



Returns beneficiary details.



Invalid ID:



HTTP 404



\---



\## Create Beneficiary



POST /beneficiaries



Required Roles:



\- FIELD\_OFFICER

\- ADMIN



Request:



Beneficiary JSON object.



Response:



Created beneficiary.



\---



\## Update Beneficiary



PUT /beneficiaries/{id}



Required Roles:



\- FIELD\_OFFICER

\- ADMIN



Request:



Updated beneficiary JSON object.



Response:



Updated beneficiary.



\---



\## Delete Beneficiary



DELETE /beneficiaries/{id}



Required Role:



\- ADMIN



Response:



Beneficiary deleted successfully.



\---



\## Upload Beneficiary Document



POST /beneficiaries/{id}/document



Required Roles:



\- FIELD\_OFFICER

\- ADMIN



Request:



multipart/form-data



file = <document>



\---



\## Download Beneficiary Document



GET /beneficiaries/{id}/document



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Response:



Beneficiary document.



\---



\# 3. Beneficiary Database Integration



\## Verify Beneficiary



GET /api/beneficiary-db/verify/{id}



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Response:



{

&#x20; "beneficiaryId": 1,

&#x20; "verified": true,

&#x20; "message": "Beneficiary verified successfully"

}



Invalid ID:



{

&#x20; "beneficiaryId": 999999,

&#x20; "verified": false,

&#x20; "message": "Beneficiary not found"

}



\---



\# 4. Scheme APIs



\## Get All Schemes



GET /api/schemes



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Scheme



GET /api/schemes/{id}



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Create Scheme



POST /api/schemes



Required Roles:



\- DISTRICT\_OFFICER

\- ADMIN



\---



\## Update Scheme



PUT /api/schemes/{id}



Required Roles:



\- DISTRICT\_OFFICER

\- ADMIN



\---



\## Delete Scheme



DELETE /api/schemes/{id}



Required Role:



\- ADMIN



\---



\# 5. Application APIs



\## Submit Application



POST /api/v1/applications



Required Roles:



\- FIELD\_OFFICER

\- ADMIN



\---



\## Get Applications



GET /api/v1/applications



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



\---



\## Get Application



GET /api/v1/applications/{id}



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



\---



\## Update Application



PUT /api/v1/applications/{id}



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



\---



\## Delete Application



DELETE /api/v1/applications/{id}



Required Role:



\- ADMIN



\---



\# 6. Eligibility APIs



\## Get All Eligibility Records



GET /eligibility



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Eligibility by Application



GET /eligibility/{applicationId}



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\# 7. Verification APIs



\## Create Verification



POST /verifications/create



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Parameters:



applicationId

officerRole



\---



\## Get All Verifications



GET /verifications



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Verification



GET /verifications/{id}



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Pending Verifications



GET /verifications/pending



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Verification History



GET /verifications/application/{applicationId}/history



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Approve Verification



PUT /verifications/{id}/approve



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Parameters:



remarks

role



\---



\## Reject Verification



PUT /verifications/{id}/reject



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Parameters:



remarks

role



\---



\## Re-verify



PUT /verifications/{id}/reverify



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- ADMIN



Parameters:



remarks



\---



\## Escalate Verification



PUT /verifications/{id}/escalate



Required Roles:



\- DISTRICT\_OFFICER

\- ADMIN



\---



\# 8. Disbursement APIs



\## Create Disbursement Plan



POST /api/disbursement-plans



Required Roles:



\- DISTRICT\_OFFICER

\- ADMIN



\---



\## Release Installment



POST /api/disbursement-plans/release/{installmentId}



Required Roles:



\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get All Plans



GET /api/disbursement-plans



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get All Installments



GET /api/disbursement-plans/installments/all



Required Roles:



\- FIELD\_OFFICER

\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Plan Installments



GET /api/disbursement-plans/{planId}/installments



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Plan



GET /api/disbursement-plans/{id}



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\# 9. Finance Approval APIs



\## Create Finance Approval



POST /finance/create



Required Roles:



\- DISTRICT\_OFFICER

\- ADMIN



Parameters:



applicationId

financeOfficer



\---



\## Get Finance Approvals



GET /finance



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Get Finance Approval



GET /finance/id/{id}



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Approve Finance Request



PUT /finance/{id}/approve



Required Roles:



\- FINANCE\_APPROVER

\- ADMIN



Parameters:



remarks



\---



\## Reject Finance Request



PUT /finance/{id}/reject



Required Roles:



\- FINANCE\_APPROVER

\- ADMIN



Parameters:



remarks



\---



\## Pending Finance Approvals



GET /finance/pending



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Approved Finance Approvals



GET /finance/approved



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\## Rejected Finance Approvals



GET /finance/rejected



Required Roles:



\- DISTRICT\_OFFICER

\- FINANCE\_APPROVER

\- ADMIN



\---



\# 10. Treasury Integration



\## Release Funds



POST /api/treasury/release-funds



Required Roles:



\- FINANCE\_APPROVER

\- ADMIN



Request:



{

&#x20; "beneficiaryId": 1,

&#x20; "amount": 5000.00,

&#x20; "referenceId": "TRX-001"

}



Success Response:



{

&#x20; "success": true,

&#x20; "beneficiaryId": 1,

&#x20; "amount": 5000.00,

&#x20; "referenceId": "TRX-001",

&#x20; "message": "Funds released successfully through mock Treasury service"

}



Invalid beneficiary:



HTTP 404



Invalid request:



HTTP 400



\---



\# 11. Security Testing



For every protected endpoint, test using JWT tokens belonging to:



1\. FIELD\_OFFICER

2\. DISTRICT\_OFFICER

3\. FINANCE\_APPROVER

4\. ADMIN



Expected result:



Authorized role:

HTTP 200 / appropriate success status.



Unauthorized role:

HTTP 403 Forbidden.



Missing/invalid token:

HTTP 401 Unauthorized.



\---



\# 12. Error Handling



Common errors:



\## Resource Not Found



HTTP 404



Example:



{

&#x20; "timestamp": "...",

&#x20; "status": 404,

&#x20; "message": "Beneficiary not found with ID: 999999"

}



\## Validation Error



HTTP 400



Returned when required request fields are missing or invalid.



\## Business Error



HTTP 400



Returned for invalid business operations.



\## Unexpected Error



HTTP 500



Returned when an unexpected server-side error occurs.

