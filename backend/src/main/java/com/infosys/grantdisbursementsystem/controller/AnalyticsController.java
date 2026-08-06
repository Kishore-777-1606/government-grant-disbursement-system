package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.dto.ApplicationSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.BudgetExhaustionDTO;
import com.infosys.grantdisbursementsystem.dto.CategoryDistributionDTO;
import com.infosys.grantdisbursementsystem.dto.DashboardSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.DisbursementSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.FundUtilizationDTO;
import com.infosys.grantdisbursementsystem.dto.MilestoneSummaryDTO;
import com.infosys.grantdisbursementsystem.dto.RegionUtilizationDTO;

import com.infosys.grantdisbursementsystem.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;



@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {


 private final AnalyticsService analyticsService;

public AnalyticsController(AnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
}

    // ================= DASHBOARD SUMMARY =================

    @GetMapping("/dashboard-summary")
    public DashboardSummaryDTO getDashboardSummary(){

        return analyticsService.getDashboardSummary();

    }



    // ================= APPLICATION SUMMARY =================

    @GetMapping("/application-summary")
    public ApplicationSummaryDTO getApplicationSummary(){

        return analyticsService.getApplicationSummary();

    }



    // ================= DISBURSEMENT SUMMARY =================

    @GetMapping("/disbursement-summary")
    public DisbursementSummaryDTO getDisbursementSummary(){

        return analyticsService.getDisbursementSummary();

    }



    // ================= MILESTONE SUMMARY =================

    @GetMapping("/milestone-summary")
    public MilestoneSummaryDTO getMilestoneSummary(){

        return analyticsService.getMilestoneSummary();

    }



    // ================= FUND UTILIZATION =================

    @GetMapping("/fund-utilization")
    public List<FundUtilizationDTO> getFundUtilization(){

        return analyticsService.getFundUtilization();

    }



    // ================= REGION UTILIZATION =================

    @GetMapping("/region-utilization")
    public List<RegionUtilizationDTO> getRegionUtilization(){

        return analyticsService.getRegionUtilization();

    }



    // ================= CATEGORY DISTRIBUTION =================

    @GetMapping("/category-distribution")
    public List<CategoryDistributionDTO> getCategoryDistribution(){

        return analyticsService.getCategoryDistribution();

    }



    // ================= BUDGET EXHAUSTION =================

    @GetMapping("/budget-exhaustion")
    public List<BudgetExhaustionDTO> getBudgetExhaustion(){

        return analyticsService.getBudgetExhaustion();

    }



    // ================= APPROVAL TURNAROUND =================

    @GetMapping("/approval-turnaround")
    public List<Map<String,Object>> getApprovalTurnaround(){

        List<Map<String,Object>> data = new ArrayList<>();

        Map<String,Object> approval = new HashMap<>();

        approval.put("stage", "Verification");
        approval.put("days", 5);

        data.add(approval);

        return data;
    }




    // ================= RECENT ACTIVITIES =================

    @GetMapping("/recent-activities")
    public List<Map<String,Object>> getRecentActivities(){

        List<Map<String,Object>> activities = new ArrayList<>();

        Map<String,Object> activity = new HashMap<>();

        activity.put(
                "message",
                "Application approved"
        );

        activity.put(
                "date",
                "2026-08-06"
        );


        activities.add(activity);


        return activities;
    }



}