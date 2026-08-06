package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.entity.*;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;


@Service
public class DisbursementPlanService {


    private final DisbursementPlanRepository planRepository;
    private final DisbursementInstallmentRepository installmentRepository;
    private final ComplianceMilestoneRepository milestoneRepository;
    private final ApplicationRepository applicationRepository;



    public DisbursementPlanService(
            DisbursementPlanRepository planRepository,
            DisbursementInstallmentRepository installmentRepository,
            ComplianceMilestoneRepository milestoneRepository,
            ApplicationRepository applicationRepository
    ) {

        this.planRepository = planRepository;
        this.installmentRepository = installmentRepository;
        this.milestoneRepository = milestoneRepository;
        this.applicationRepository = applicationRepository;

    }





    public DisbursementPlan createPlan(
            Application application,
            Double totalAmount,
            Integer numInstallments
    ) {


        Objects.requireNonNull(application);
        Objects.requireNonNull(totalAmount);
        Objects.requireNonNull(numInstallments);



        DisbursementPlan plan = new DisbursementPlan();

        plan.setApplication(application);
        plan.setTotalGrantAmount(totalAmount);
        plan.setNumberOfInstallments(numInstallments);
        plan.setCreatedDate(LocalDate.now());
        plan.setStatus("Active");


        plan = planRepository.save(plan);



        double baseAmount =
                Math.floor(
                        (totalAmount / numInstallments) * 100
                ) / 100.0;



        double allocatedSoFar = 0;



        for(int i = 1; i <= numInstallments; i++) {


            double installmentAmount;



            if(i == numInstallments) {

                installmentAmount =
                        totalAmount - allocatedSoFar;

            }
            else {

                installmentAmount = baseAmount;

                allocatedSoFar += baseAmount;

            }



            ComplianceMilestone milestone =
                    new ComplianceMilestone();


            milestone.setApplication(application);

            milestone.setMilestoneType(
                    milestoneTypeForInstallment(i)
            );

            milestone.setStatus("Pending");

            milestone.setDueDate(
                    LocalDate.now()
                    .plusDays(30L * i)
            );


            milestone =
                    milestoneRepository.save(milestone);




            DisbursementInstallment installment =
                    new DisbursementInstallment();


            installment.setDisbursementPlan(plan);

            installment.setMilestone(milestone);

            installment.setInstallmentNumber(i);

            installment.setInstallmentAmount(
                    installmentAmount
            );

            installment.setScheduledDate(
                    LocalDate.now()
                    .plusDays(30L * i)
            );

            installment.setStatus("Scheduled");



            installmentRepository.save(installment);

        }



        application.setStatus(
                "Disbursement In Progress"
        );


        applicationRepository.save(application);



        return plan;

    }








    private String milestoneTypeForInstallment(
            int installmentNumber
    ) {


        if(installmentNumber == 1)
            return "Documentation";


        if(installmentNumber == 2)
            return "Ground Verification";


        return "Utilization Proof";

    }








    public DisbursementInstallment releaseInstallmentIfMilestoneComplete(
            Long installmentId
    ) {



        Long id = Objects.requireNonNull(installmentId);



        DisbursementInstallment installment =
                installmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Installment not found with ID: "
                                + id
                        )
                );



        if(installment.getMilestone() == null) {

            throw new IllegalStateException(
                    "Installment has no linked milestone"
            );

        }




        if(!"Completed".equalsIgnoreCase(
                installment.getMilestone().getStatus()
        )) {


            throw new IllegalStateException(
                    "Milestone not yet completed"
            );

        }




        installment.setStatus("Released");

        installment.setActualReleaseDate(
                LocalDate.now()
        );


        installmentRepository.save(installment);




        checkAndUpdatePlanCompletion(
                installment.getDisbursementPlan()
                .getPlanId()
        );



        return installment;

    }








    private void checkAndUpdatePlanCompletion(
            Long planId
    ) {


        Long id = Objects.requireNonNull(planId);



        DisbursementPlan plan =
                planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plan not found with ID: "
                                + id
                        )
                );




        var installments =
                installmentRepository
                .findByDisbursementPlanPlanId(id);




        boolean allReleased =
                installments.stream()
                .allMatch(inst ->
                        "Released"
                        .equalsIgnoreCase(
                                inst.getStatus()
                        )
                );




        if(allReleased) {

            plan.setStatus("Completed");

            planRepository.save(plan);

        }

    }

}