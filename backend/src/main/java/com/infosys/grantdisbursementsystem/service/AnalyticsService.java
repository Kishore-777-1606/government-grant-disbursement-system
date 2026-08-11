package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.dto.*;
import com.infosys.grantdisbursementsystem.entity.*;
import com.infosys.grantdisbursementsystem.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
public class AnalyticsService {


    private final BeneficiaryRepository beneficiaryRepository;
    private final ApplicationRepository applicationRepository;
    private final SchemeRepository schemeRepository;
    private final DisbursementPlanRepository disbursementPlanRepository;
    private final DisbursementInstallmentRepository installmentRepository;
    private final ComplianceMilestoneRepository milestoneRepository;
    private final VerificationRepository verificationRepository;
    private final FinanceApprovalRepository financeApprovalRepository;



    public AnalyticsService(
            BeneficiaryRepository beneficiaryRepository,
            ApplicationRepository applicationRepository,
            SchemeRepository schemeRepository,
            DisbursementPlanRepository disbursementPlanRepository,
            DisbursementInstallmentRepository installmentRepository,
            ComplianceMilestoneRepository milestoneRepository,
            VerificationRepository verificationRepository,
            FinanceApprovalRepository financeApprovalRepository
    ) {

        this.beneficiaryRepository = beneficiaryRepository;
        this.applicationRepository = applicationRepository;
        this.schemeRepository = schemeRepository;
        this.disbursementPlanRepository = disbursementPlanRepository;
        this.installmentRepository = installmentRepository;
        this.milestoneRepository = milestoneRepository;
        this.verificationRepository = verificationRepository;
        this.financeApprovalRepository = financeApprovalRepository;

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


        long rejected = applications.stream()
                .filter(a ->
                        "Rejected".equalsIgnoreCase(a.getStatus())
                        ||
                        "Not Eligible".equalsIgnoreCase(a.getStatus()))
                .count();


        // The workflow never sets a literal "Pending" status on Application —
        // it uses stage-specific labels instead (e.g. "Field Verification
        // Pending", "Finance Approval Pending", "Disbursement In Progress").
        // "Pending" here means "still in flight": anything that hasn't
        // reached a final approval or rejection outcome.
        long pending = total - approved - rejected;



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

    public MilestoneSummaryDTO getMilestoneSummary() {

    List<ComplianceMilestone> list =
            milestoneRepository.findAll();

    LocalDate today = LocalDate.now();

    long total =
            list.size();

    /*
     * A milestone is considered overdue when:
     * 1. It has already been explicitly flagged as "Overdue", OR
     * 2. It is still "Pending" but its due date has passed.
     *
     * The second condition makes the analytics accurate even if the
     * scheduled overdue job has not executed yet.
     */
    long overdue =
            list.stream()
            .filter(m ->
                    "Overdue".equalsIgnoreCase(m.getStatus())
                    ||
                    (
                        "Pending".equalsIgnoreCase(m.getStatus())
                        &&
                        m.getDueDate() != null
                        &&
                        m.getDueDate().isBefore(today)
                    )
            )
            .count();

    /*
     * Pending means a milestone that is still pending and whose
     * due date has not passed yet.
     *
     * This keeps Pending and Overdue as separate categories.
     */
    long pending =
            list.stream()
            .filter(m ->
                    "Pending".equalsIgnoreCase(m.getStatus())
                    &&
                    (
                        m.getDueDate() == null
                        ||
                        !m.getDueDate().isBefore(today)
                    )
            )
            .count();

    long completed =
            list.stream()
            .filter(m ->
                    "Completed".equalsIgnoreCase(m.getStatus()))
            .count();

    return new MilestoneSummaryDTO(
            total,
            pending,
            completed,
            overdue
    );
}
        // ================= REGION UTILIZATION =================

    // ================= REGION UTILIZATION =================

public List<RegionUtilizationDTO> getRegionUtilization() {

    /*
     * Milestone 3 requires region-wise fund utilization.
     *
     * Utilization means the amount that has actually been released,
     * not the total grant amount planned for a disbursement plan.
     *
     * Therefore, we calculate regional utilization using only
     * installments whose status is "Released".
     */

    Map<Long, Double> regionReleasedAmount = new HashMap<>();


    List<DisbursementPlan> plans =
            disbursementPlanRepository.findAll();


    List<DisbursementInstallment> installments =
            installmentRepository.findAll();


    for (DisbursementPlan plan : plans) {

        Application app =
                plan.getApplication();


        if (app == null) {
            continue;
        }


        Long beneficiaryId =
                app.getBeneficiaryId();


        if (beneficiaryId == null) {
            continue;
        }


        Beneficiary beneficiary =
                beneficiaryRepository.findById(
                        beneficiaryId
                )
                .orElse(null);


        if (beneficiary == null) {
            continue;
        }


        Long stateId =
                beneficiary.getStateId();


        if (stateId == null) {
            continue;
        }


        /*
         * Find the installments belonging to this plan
         * that have actually been released.
         */
        double releasedAmount =
                installments.stream()
                .filter(i ->
                        i.getDisbursementPlan() != null
                        &&
                        i.getDisbursementPlan()
                                .getPlanId()
                                .equals(plan.getPlanId())
                )
                .filter(i ->
                        "Released".equalsIgnoreCase(
                                i.getStatus()
                        )
                )
                .mapToDouble(i ->
                        i.getInstallmentAmount() != null
                        ?
                        i.getInstallmentAmount()
                        :
                        0.0
                )
                .sum();


        /*
         * Add the released amount to the beneficiary's region.
         *
         * Multiple disbursement plans belonging to the same
         * state are therefore aggregated together.
         */
        regionReleasedAmount.put(
                stateId,
                regionReleasedAmount.getOrDefault(
                        stateId,
                        0.0
                ) + releasedAmount
        );

    }


    List<RegionUtilizationDTO> result =
            new ArrayList<>();


    regionReleasedAmount.forEach(
            (stateId, releasedAmount) -> {

                result.add(
                        new RegionUtilizationDTO(
                                "State-" + stateId,
                                releasedAmount
                        )
                );

            }
    );


    return result;
}



    // ================= CATEGORY DISTRIBUTION =================

    public List<CategoryDistributionDTO> getCategoryDistribution(){


        Map<String,Long> map =
                new HashMap<>();



        // Module 4 asks for "beneficiary category-wise distribution" —
        // grouped by the beneficiary's own category (General/SC/ST/OBC/
        // EWS), not the scheme's type. Each application is looked up via
        // its beneficiaryId.
        for(Application app :
                applicationRepository.findAll()){


            if(app.getBeneficiaryId()==null)
                continue;



            Beneficiary beneficiary =
                    beneficiaryRepository.findById(
                            Objects.requireNonNull(
                                    app.getBeneficiaryId()
                            )
                    )
                    .orElse(null);



            if(beneficiary!=null && beneficiary.getCategory()!=null){


                String category =
                        beneficiary.getCategory();



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





    // ================= APPROVAL TURNAROUND =================

    public List<Map<String,Object>> getApprovalTurnaround(){


        List<Map<String,Object>> result =
                new ArrayList<>();



        // Stage 1: Application submitted -> first verification stage recorded.
        // Each application can now have multiple Verification rows (one per
        // stage), so this must use only the FIRST stage per application —
        // otherwise later stages (District Officer, re-verification, etc.)
        // would double/triple-count and skew the average.

        List<Long> verificationDays =
                new ArrayList<>();


        for(Application app : applicationRepository.findAll()){


            if(app.getApplicationDate() == null)
                continue;


            Verification firstStage =
                    verificationRepository
                    .findFirstByApplicationOrderByVerificationIdAsc(app)
                    .orElse(null);


            if(firstStage == null
                    ||
               firstStage.getVerificationDate() == null)
                continue;


            long days =
                    ChronoUnit.DAYS.between(
                            app.getApplicationDate(),
                            firstStage.getVerificationDate()
                    );

            if(days >= 0)
                verificationDays.add(days);

        }


        if(!verificationDays.isEmpty()){

            double avgDays =
                    verificationDays.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0);

            Map<String,Object> stage =
                    new HashMap<>();

            stage.put("stage", "Verification");
            stage.put("days", Math.round(avgDays * 10.0) / 10.0);

            result.add(stage);

        }



        // Stage 2: Latest verification stage recorded -> Finance approval
        // decided. Uses the most recent Verification row per application
        // (typically the District Officer's approval, which is what
        // triggers the Finance Approval record) rather than assuming a
        // single row exists.

        List<Long> financeDays =
                new ArrayList<>();


        for(FinanceApproval fa : financeApprovalRepository.findAll()){


            boolean decided =
                    "Approved".equalsIgnoreCase(fa.getApprovalStatus())
                    ||
                    "Rejected".equalsIgnoreCase(fa.getApprovalStatus());


            if(!decided
                    ||
               fa.getApplication() == null
                    ||
               fa.getApprovalDate() == null)
                continue;


            Verification latestStage =
                    verificationRepository
                    .findFirstByApplicationOrderByVerificationIdDesc(fa.getApplication())
                    .orElse(null);


            if(latestStage == null || latestStage.getVerificationDate() == null)
                continue;


            long days =
                    ChronoUnit.DAYS.between(
                            latestStage.getVerificationDate(),
                            fa.getApprovalDate()
                    );


            if(days >= 0)
                financeDays.add(days);

        }


        if(!financeDays.isEmpty()){

            double avgDays =
                    financeDays.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0);

            Map<String,Object> stage =
                    new HashMap<>();

            stage.put("stage", "Finance Approval");
            stage.put("days", Math.round(avgDays * 10.0) / 10.0);

            result.add(stage);

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