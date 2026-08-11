package com.infosys.grantdisbursementsystem.service.impl;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.entity.Scheme;

import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.BeneficiaryRepository;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;

import com.infosys.grantdisbursementsystem.service.ApplicationService;
import com.infosys.grantdisbursementsystem.service.EligibilityScoringService;
import com.infosys.grantdisbursementsystem.service.VerificationService;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class ApplicationServiceImpl implements ApplicationService {


    private final ApplicationRepository repository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final SchemeRepository schemeRepository;

    private final EligibilityScoringService eligibilityScoringService;

    private final VerificationService verificationService;



    public ApplicationServiceImpl(
            ApplicationRepository repository,
            BeneficiaryRepository beneficiaryRepository,
            SchemeRepository schemeRepository,
            EligibilityScoringService eligibilityScoringService,
            VerificationService verificationService
    ) {

        this.repository = repository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.schemeRepository = schemeRepository;
        this.eligibilityScoringService = eligibilityScoringService;
        this.verificationService = verificationService;

    }




    @Override
    public Application submitApplication(
            Application application
    ) {


        Objects.requireNonNull(
                application,
                "Application cannot be null"
        );



        if(application.getBeneficiaryId() == null){

            throw new IllegalArgumentException(
                    "Beneficiary ID is required"
            );

        }



        if(application.getSchemeId() == null){

            throw new IllegalArgumentException(
                    "Scheme ID is required"
            );

        }




        Beneficiary beneficiary =
                beneficiaryRepository.findById(
                        Objects.requireNonNull(
                                application.getBeneficiaryId()
                        )
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Beneficiary not found with ID: "
                                + application.getBeneficiaryId()
                        )
                );





        Scheme scheme =
                schemeRepository.findById(
                        Objects.requireNonNull(
                                application.getSchemeId()
                        )
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Scheme not found with ID: "
                                + application.getSchemeId()
                        )
                );



// Default the applied amount to the scheme's grant amount when the
        // caller didn't supply one, so downstream routing/disbursement
        // always has an amount to work with. When the caller DID supply one
        // (a per-application requested amount), it must not exceed what the
        // scheme actually grants.
        if(application.getAppliedAmount() == null) {

            application.setAppliedAmount(scheme.getAmount());

        }
        else if(scheme.getAmount() != null
                && application.getAppliedAmount().compareTo(scheme.getAmount()) > 0) {

            throw new IllegalArgumentException(
                    "Requested amount cannot exceed the scheme's grant amount of "
                    + scheme.getAmount()
            );

        }



        double score =
                eligibilityScoringService.calculateScore(
                        beneficiary,
                        scheme
                );



        boolean meetsCriteria =
                eligibilityScoringService.meetsSchemeCriteria(
                        beneficiary,
                        scheme
                );


        boolean eligible =
                eligibilityScoringService.isEligible(
                        score,
                        beneficiary,
                        scheme
                );




        application.setEligibilityScore(score);


        application.setStatus(
                eligible
                        ? "Eligible"
                        : "Not Eligible"
        );


        // Make it clear in the record WHY an application was rejected -
        // failing the scheme's own criteria (income/category) is a
        // different reason than simply scoring too low.
        if(!eligible && !meetsCriteria) {

            application.setRemarks(
                    "Does not meet this scheme's eligibility criteria "
                    + "(income limit and/or beneficiary category)"
            );

        }
        else if(!eligible) {

            application.setRemarks(
                    "Eligibility score below the required threshold"
            );

        }




        Application saved =
                repository.save(application);





        if(eligible && saved.getApplicationId() != null){


            verificationService.createVerification(
                    Objects.requireNonNull(
                            saved.getApplicationId()
                    ),
                    null
            );



            saved =
                    repository.findById(
                            Objects.requireNonNull(
                                    saved.getApplicationId()
                            )
                    )
                    .orElse(saved);

        }



        return saved;

    }





    @Override
    public List<Application> getAllApplications(){

        return repository.findAll();

    }






    @Override
    public Application getApplicationById(
            @NonNull Long id
    ){

        return repository.findById(
                Objects.requireNonNull(id)
        )
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Application not found with ID: "
                        + id
                )
        );

    }







    @Override
    public Application updateApplication(
            @NonNull Long id,
            Application application
    ){

        Objects.requireNonNull(application,
                "Application cannot be null");



        if(!repository.existsById(
                Objects.requireNonNull(id)
        )){

            throw new ResourceNotFoundException(
                    "Application not found with ID: "
                    + id
            );

        }



        application.setApplicationId(id);



        return repository.save(application);

    }







    @Override
    public void deleteApplication(
            @NonNull Long id
    ){

        if(!repository.existsById(
                Objects.requireNonNull(id)
        )){


            throw new ResourceNotFoundException(
                    "Application not found with ID: "
                    + id
            );

        }



        repository.deleteById(
                Objects.requireNonNull(id)
        );

    }


}