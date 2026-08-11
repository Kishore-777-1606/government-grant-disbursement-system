package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.dto.EligibilityView;
import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.BeneficiaryRepository;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class EligibilityService {


    private final ApplicationRepository applicationRepository;

    private final BeneficiaryRepository beneficiaryRepository;

    private final SchemeRepository schemeRepository;




    public EligibilityService(
            ApplicationRepository applicationRepository,
            BeneficiaryRepository beneficiaryRepository,
            SchemeRepository schemeRepository
    ) {

        this.applicationRepository = applicationRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.schemeRepository = schemeRepository;

    }





    public List<EligibilityView> getAllEligibilityRecords() {


        return applicationRepository.findAll()
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());

    }







    public EligibilityView getEligibilityByApplicationId(
            @NonNull Long applicationId
    ) {


        Application application =
                applicationRepository.findById(
                        Objects.requireNonNull(applicationId)
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with ID: "
                                + applicationId
                        )
                );



        return toView(application);

    }







    private EligibilityView toView(
            Application application
    ) {


        Objects.requireNonNull(
                application,
                "Application cannot be null"
        );



        String beneficiaryName = "Unknown";



        if(application.getBeneficiaryId() != null) {


            Long beneficiaryId =
                    Objects.requireNonNull(
                            application.getBeneficiaryId()
                    );



            beneficiaryName =
                    beneficiaryRepository.findById(
                            beneficiaryId
                    )
                    .map(this::fullName)
                    .orElse("Unknown");

        }






        String schemeName = "Unknown";



        if(application.getSchemeId() != null) {


            Long schemeId =
                    Objects.requireNonNull(
                            application.getSchemeId()
                    );



            schemeName =
                    schemeRepository.findById(
                            schemeId
                    )
                    .map(scheme -> scheme.getName())
                    .orElse("Unknown");

        }






        return new EligibilityView(

                application.getApplicationId(),

                application.getBeneficiaryId(),

                beneficiaryName,

                application.getSchemeId(),

                schemeName,

                application.getEligibilityScore(),

                application.getStatus(),

                application.getApplicationDate()

        );

    }







    private String fullName(
            Beneficiary beneficiary
    ) {


        Objects.requireNonNull(
                beneficiary,
                "Beneficiary cannot be null"
        );



        String lastName =
                beneficiary.getLastName() != null
                ?
                " " + beneficiary.getLastName()
                :
                "";



        return beneficiary.getFirstName()
                + lastName;

    }


}