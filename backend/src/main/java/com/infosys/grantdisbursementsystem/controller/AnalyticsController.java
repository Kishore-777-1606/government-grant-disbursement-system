package com.infosys.grantdisbursementsystem.controller;
import com.infosys.grantdisbursementsystem.dto.DisbursementSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.DashboardSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.ApplicationSummaryDTO;
import com.infosys.grantdisbursementsystem.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.infosys.grantdisbursementsystem.dto.MilestoneSummaryDTO;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/application-summary")
    public ApplicationSummaryDTO getApplicationSummary() {
        return analyticsService.getApplicationSummary();
    }
    @GetMapping("/disbursement-summary")
public DisbursementSummaryDTO getDisbursementSummary() {
    return analyticsService.getDisbursementSummary();
}
    @GetMapping("/dashboard-summary")
public DashboardSummaryDTO getDashboardSummary() {
    return analyticsService.getDashboardSummary();
}
@GetMapping("/milestone-summary")
public MilestoneSummaryDTO getMilestoneSummary() {
    return analyticsService.getMilestoneSummary();
}
}