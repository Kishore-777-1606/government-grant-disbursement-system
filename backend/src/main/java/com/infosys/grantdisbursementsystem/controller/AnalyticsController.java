package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.dto.RecentActivityDTO;
import com.infosys.grantdisbursementsystem.dto.ApplicationSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.BudgetExhaustionDTO;
import com.infosys.grantdisbursementsystem.dto.CategoryDistributionDTO;
import com.infosys.grantdisbursementsystem.dto.DashboardSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.DisbursementSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.FundUtilizationDTO;
import com.infosys.grantdisbursementsystem.dto.MilestoneSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.RegionUtilizationDTO;

import com.infosys.grantdisbursementsystem.service.AnalyticsService;
import com.infosys.grantdisbursementsystem.service.ReportExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {


 private final AnalyticsService analyticsService;

 private final ReportExportService reportExportService;

public AnalyticsController(
        AnalyticsService analyticsService,
        ReportExportService reportExportService
) {
    this.analyticsService = analyticsService;
    this.reportExportService = reportExportService;
}

     // ================= DASHBOARD SUMMARY =================
    // Restricted to DISTRICT_OFFICER, FINANCE_APPROVER, ADMIN — Field
    // Officers are a ground-verification role and don't need reporting/
    // analytics access. Applies consistently to every endpoint below.

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/dashboard-summary")
    public DashboardSummaryDTO getDashboardSummary(){

        return analyticsService.getDashboardSummary();

    }



    // ================= APPLICATION SUMMARY =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/application-summary")
    public ApplicationSummaryDTO getApplicationSummary(){

        return analyticsService.getApplicationSummary();

    }



    // ================= DISBURSEMENT SUMMARY =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/disbursement-summary")
    public DisbursementSummaryDTO getDisbursementSummary(){

        return analyticsService.getDisbursementSummary();

    }



    // ================= MILESTONE SUMMARY =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/milestone-summary")
    public MilestoneSummaryDTO getMilestoneSummary(){

        return analyticsService.getMilestoneSummary();

    }



    // ================= FUND UTILIZATION =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/fund-utilization")
    public List<FundUtilizationDTO> getFundUtilization(){

        return analyticsService.getFundUtilization();

    }



    // ================= REGION UTILIZATION =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/region-utilization")
    public List<RegionUtilizationDTO> getRegionUtilization(){

        return analyticsService.getRegionUtilization();

    }



    // ================= CATEGORY DISTRIBUTION =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/category-distribution")
    public List<CategoryDistributionDTO> getCategoryDistribution(){

        return analyticsService.getCategoryDistribution();

    }



    // ================= BUDGET EXHAUSTION =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/budget-exhaustion")
    public List<BudgetExhaustionDTO> getBudgetExhaustion(){

        return analyticsService.getBudgetExhaustion();

    }



    // ================= APPROVAL TURNAROUND =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/approval-turnaround")
    public List<Map<String,Object>> getApprovalTurnaround(){

        return analyticsService.getApprovalTurnaround();
    }




    // ================= RECENT ACTIVITIES =================
// ================= RECENT ACTIVITIES =================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/recent-activities")
public List<RecentActivityDTO> getRecentActivities() {

    return analyticsService.getRecentActivities();

}



    // ================= EXPORTS (Module 4: downloadable PDF/Excel reports) =================
@PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/export/fund-utilization/excel")
    public ResponseEntity<byte[]> exportFundUtilizationExcel() {

        byte[] file = reportExportService.fundUtilizationToExcel(
                analyticsService.getFundUtilization()
        );

        return fileResponse(
                file,
                "scheme-wise-fund-utilization.xlsx",
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );

    }
@PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/export/fund-utilization/pdf")
    public ResponseEntity<byte[]> exportFundUtilizationPdf() {

        byte[] file = reportExportService.fundUtilizationToPdf(
                analyticsService.getFundUtilization()
        );

        return fileResponse(file, "scheme-wise-fund-utilization.pdf", MediaType.APPLICATION_PDF);

    }

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/export/region-utilization/excel")
    public ResponseEntity<byte[]> exportRegionUtilizationExcel() {

        byte[] file = reportExportService.regionUtilizationToExcel(
                analyticsService.getRegionUtilization()
        );

        return fileResponse(
                file,
                "region-wise-disbursement-summary.xlsx",
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );

    }

   @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/export/region-utilization/pdf")
    public ResponseEntity<byte[]> exportRegionUtilizationPdf() {

        byte[] file = reportExportService.regionUtilizationToPdf(
                analyticsService.getRegionUtilization()
        );

        return fileResponse(file, "region-wise-disbursement-summary.pdf", MediaType.APPLICATION_PDF);

    }

    private ResponseEntity<byte[]> fileResponse(byte[] file, String filename, MediaType mediaType) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(file);

    }

}