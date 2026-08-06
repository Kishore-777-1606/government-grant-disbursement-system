package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.dto.*;
import com.infosys.grantdisbursementsystem.entity.*;
import com.infosys.grantdisbursementsystem.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


@Service
public class AnalyticsService {


    private final BeneficiaryRepository beneficiaryRepository;
    private final ApplicationRepository applicationRepository;
    private final SchemeRepository schemeRepository;
    private final DisbursementPlanRepository disbursementPlanRepository;
    private final DisbursementInstallmentRepository installmentRepository;
    private final ComplianceMilestoneRepository milestoneRepository;



    public AnalyticsService(
            BeneficiaryRepository beneficiaryRepository,
            ApplicationRepository applicationRepository,
            SchemeRepository schemeRepository,
            DisbursementPlanRepository disbursementPlanRepository,
            DisbursementInstallmentRepository installmentRepository,
            ComplianceMilestoneRepository milestoneRepository
    ) {

        this.beneficiaryRepository = beneficiaryRepository;
        this.applicationRepository = applicationRepository;
        this.schemeRepository = schemeRepository;
        this.disbursementPlanRepository = disbursementPlanRepository;
        this.installmentRepository = installmentRepository;
        this.milestoneRepository = milestoneRepository;

    }



    // ================= APPLICATION SUMMARY =================

    public ApplicationSummaryDTO getApplicationSummary() {

        List<Application> applications =
                applicationRepository.findAll();


        long total = applications.size();


        long approved = applications.stream()
                .filter(a ->
                        "Approved".equalsIgnoreCase(a.getStatus()))
                .count();


        long pending = applications.stream()
                .filter(a ->
                        "Pending".equalsIgnoreCase(a.getStatus()))
                .count();


        long rejected = applications.stream()
                .filter(a ->
                        "Rejected".equalsIgnoreCase(a.getStatus()))
                .count();



        return new ApplicationSummaryDTO(
                total,
                approved,
                pending,
                rejected
        );
    }





    // ================= DASHBOARD SUMMARY =================

    public DashboardSummaryDTO getDashboardSummary() {


        long beneficiaries =
                beneficiaryRepository.count();


        long schemes =
                schemeRepository.count();


        long applications =
                applicationRepository.count();



        long activePlans =
                disbursementPlanRepository.findAll()
                .stream()
                .filter(p ->
                        "Active".equalsIgnoreCase(p.getStatus()))
                .count();



        long completedPlans =
                disbursementPlanRepository.findAll()
                .stream()
                .filter(p ->
                        "Completed".equalsIgnoreCase(p.getStatus()))
                .count();



        long pendingMilestones =
                milestoneRepository.findAll()
                .stream()
                .filter(m ->
                        "Pending".equalsIgnoreCase(m.getStatus()))
                .count();



        long releasedInstallments =
                installmentRepository.findAll()
                .stream()
                .filter(i ->
                        "Released".equalsIgnoreCase(i.getStatus()))
                .count();



        return new DashboardSummaryDTO(
                beneficiaries,
                schemes,
                applications,
                activePlans,
                completedPlans,
                pendingMilestones,
                releasedInstallments
        );

    }





    // ================= DISBURSEMENT SUMMARY =================

    public DisbursementSummaryDTO getDisbursementSummary(){


        List<DisbursementPlan> plans =
                disbursementPlanRepository.findAll();


        List<DisbursementInstallment> installments =
                installmentRepository.findAll();



        long totalPlans =
                plans.size();


        long activePlans =
                plans.stream()
                .filter(p ->
                        "Active".equalsIgnoreCase(p.getStatus()))
                .count();



        long completedPlans =
                plans.stream()
                .filter(p ->
                        "Completed".equalsIgnoreCase(p.getStatus()))
                .count();



        long totalInstallments =
                installments.size();



        long releasedInstallments =
                installments.stream()
                .filter(i ->
                        "Released".equalsIgnoreCase(i.getStatus()))
                .count();



        double totalAmount =
                plans.stream()
                .mapToDouble(p ->
                        p.getTotalGrantAmount()!=null
                        ?
                        p.getTotalGrantAmount()
                        :
                        0)
                .sum();



        double releasedAmount =
                installments.stream()
                .filter(i ->
                        "Released".equalsIgnoreCase(i.getStatus()))
                .mapToDouble(i ->
                        i.getInstallmentAmount()!=null
                        ?
                        i.getInstallmentAmount()
                        :
                        0)
                .sum();



        return new DisbursementSummaryDTO(
                totalPlans,
                activePlans,
                completedPlans,
                totalInstallments,
                releasedInstallments,
                totalAmount,
                releasedAmount
        );

    }





    // ================= MILESTONE SUMMARY =================

    public MilestoneSummaryDTO getMilestoneSummary(){


        List<ComplianceMilestone> list =
                milestoneRepository.findAll();



        long total =
                list.size();



        long pending =
                list.stream()
                .filter(m ->
                        "Pending".equalsIgnoreCase(m.getStatus()))
                .count();



        long completed =
                list.stream()
                .filter(m ->
                        "Completed".equalsIgnoreCase(m.getStatus()))
                .count();



        long overdue =
                list.stream()
                .filter(m ->
                        "Pending".equalsIgnoreCase(m.getStatus())
                        &&
                        m.getDueDate()!=null
                        &&
                        m.getDueDate().isBefore(LocalDate.now()))
                .count();



        return new MilestoneSummaryDTO(
                total,
                pending,
                completed,
                overdue
        );

    }
        // ================= REGION UTILIZATION =================

    public List<RegionUtilizationDTO> getRegionUtilization(){


        Map<Long,Double> map =
                new HashMap<>();


        for(DisbursementPlan plan :
                disbursementPlanRepository.findAll()){


            Application app =
                    plan.getApplication();


            if(app == null)
                continue;



            Long beneficiaryId =
                    app.getBeneficiaryId();



            if(beneficiaryId == null)
                continue;



            Beneficiary beneficiary =
                    beneficiaryRepository.findById(
                            Objects.requireNonNull(beneficiaryId)
                    )
                    .orElse(null);



            if(beneficiary == null)
                continue;



            Long stateId =
                    beneficiary.getStateId();



            if(stateId == null)
                continue;



            double amount =
                    plan.getTotalGrantAmount()!=null
                    ?
                    plan.getTotalGrantAmount()
                    :
                    0;



            map.put(
                    stateId,
                    map.getOrDefault(stateId,0.0)+amount
            );

        }




        List<RegionUtilizationDTO> result =
                new ArrayList<>();



        map.forEach((state,amount)->{


            result.add(
                    new RegionUtilizationDTO(
                            "State-"+state,
                            amount
                    )
            );

        });



        return result;

    }





    // ================= CATEGORY DISTRIBUTION =================

    public List<CategoryDistributionDTO> getCategoryDistribution(){


        Map<String,Long> map =
                new HashMap<>();



        for(Application app :
                applicationRepository.findAll()){


            if(app.getSchemeId()==null)
                continue;



            Scheme scheme =
                    schemeRepository.findById(
                            Objects.requireNonNull(
                                    app.getSchemeId()
                            )
                    )
                    .orElse(null);



            if(scheme!=null){


                String category =
                        scheme.getSchemeType();



                map.put(
                        category,
                        map.getOrDefault(category,0L)+1
                );

            }

        }




        List<CategoryDistributionDTO> result =
                new ArrayList<>();



        map.forEach((key,value)->{


            result.add(
                    new CategoryDistributionDTO(
                            key,
                            value
                    )
            );


        });



        return result;

    }





    // ================= FUND UTILIZATION =================

    public List<FundUtilizationDTO> getFundUtilization(){


        List<FundUtilizationDTO> result =
                new ArrayList<>();



        List<DisbursementPlan> plans =
                disbursementPlanRepository.findAll();



        List<DisbursementInstallment> installments =
                installmentRepository.findAll();




        for(DisbursementPlan plan : plans){



            Application app =
                    plan.getApplication();



            if(app == null ||
                    app.getSchemeId()==null)
                continue;




            Scheme scheme =
                    schemeRepository.findById(
                            Objects.requireNonNull(
                                    app.getSchemeId()
                            )
                    )
                    .orElse(null);



            if(scheme==null)
                continue;




            double totalAmount =
                    plan.getTotalGrantAmount()!=null
                    ?
                    plan.getTotalGrantAmount()
                    :
                    0;




            double releasedAmount =
                    installments.stream()
                    .filter(i ->
                            i.getDisbursementPlan()!=null
                            &&
                            i.getDisbursementPlan()
                            .getPlanId()
                            .equals(plan.getPlanId()))
                    .filter(i ->
                            "Released"
                            .equalsIgnoreCase(i.getStatus()))
                    .mapToDouble(i ->
                            i.getInstallmentAmount()!=null
                            ?
                            i.getInstallmentAmount()
                            :
                            0)
                    .sum();




            result.add(
                    new FundUtilizationDTO(
                            scheme.getName(),
                            totalAmount,
                            releasedAmount,
                            totalAmount-releasedAmount
                    )
            );

        }



        return result;

    }





    // ================= BUDGET EXHAUSTION =================

    public List<BudgetExhaustionDTO> getBudgetExhaustion(){


        List<BudgetExhaustionDTO> result =
                new ArrayList<>();



        for(FundUtilizationDTO fund :
                getFundUtilization()){



            double percentage =
                    fund.getTotalAmount()==0
                    ?
                    0
                    :
                    (fund.getReleasedAmount()
                    /
                    fund.getTotalAmount())
                    *
                    100;



            result.add(
                    new BudgetExhaustionDTO(
                            fund.getSchemeName(),
                            percentage
                    )
            );

        }



        return result;

    }





    // ================= RECENT ACTIVITIES =================

    public List<RecentActivityDTO> getRecentActivities(){



        List<RecentActivityDTO> activities =
                new ArrayList<>();




        applicationRepository.findAll()
                .stream()
                .limit(5)
                .forEach(app -> {


                    activities.add(
                            new RecentActivityDTO(
                                    "Application Submitted",
                                    "Application ID : "
                                    + app.getApplicationId()
                            )
                    );

                });





        beneficiaryRepository.findAll()
                .stream()
                .limit(5)
                .forEach(beneficiary -> {


                    activities.add(
                            new RecentActivityDTO(
                                    "New Beneficiary Registered",
                                    "Beneficiary ID : "
                                    + beneficiary.getId()
                            )
                    );


                });





        disbursementPlanRepository.findAll()
                .stream()
                .limit(5)
                .forEach(plan -> {


                    activities.add(
                            new RecentActivityDTO(
                                    "Disbursement Plan Created",
                                    "Plan ID : "
                                    + plan.getPlanId()
                            )
                    );


                });



        return activities;

    }


}