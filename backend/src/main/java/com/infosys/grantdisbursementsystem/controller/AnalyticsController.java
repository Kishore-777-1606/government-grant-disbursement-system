package com.infosys.grantdisbursementsystem.controller;
import com.infosys.grantdisbursementsystem.dto.DisbursementSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.DashboardSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.ApplicationSummaryDTO;
import com.infosys.grantdisbursementsystem.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.infosys.grantdisbursementsystem.dto.MilestoneSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.RegionUtilizationDTO;
import com.infosys.grantdisbursementsystem.dto.CategoryDistributionDTO;
import com.infosys.grantdisbursementsystem.dto.FundUtilizationDTO;
import com.infosys.grantdisbursementsystem.dto.BudgetExhaustionDTO;
import com.infosys.grantdisbursementsystem.dto.RecentActivityDTO;
import java.util.List;

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
@GetMapping("/region-wise-fund-utilization")
    public List<RegionUtilizationDTO> getRegionWiseFundUtilization() {
        return analyticsService.getRegionWiseFundUtilization();
    }

    @GetMapping("/category-wise-distribution")
    public List<CategoryDistributionDTO> getCategoryWiseDistribution() {
        return analyticsService.getCategoryWiseDistribution();
    }

    @GetMapping("/fund-utilization")
    public List<FundUtilizationDTO> getSchemeWiseFundUtilization() {
        return analyticsService.getSchemeWiseFundUtilization();
    }

    @GetMapping("/budget-exhaustion")
    public List<BudgetExhaustionDTO> getBudgetExhaustion() {
        return analyticsService.getBudgetExhaustion();
    }

    @GetMapping("/recent-activities")
    public List<RecentActivityDTO> getRecentActivities() {
        return analyticsService.getRecentActivities();
    }
}