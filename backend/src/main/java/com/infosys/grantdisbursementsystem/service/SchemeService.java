package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.entity.Scheme;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class SchemeService {


    private final SchemeRepository schemeRepository;



    public SchemeService(
            SchemeRepository schemeRepository
    ) {

        this.schemeRepository = schemeRepository;

    }





    public List<Scheme> getAllSchemes() {

        return schemeRepository.findAll();

    }





    public Scheme getSchemeById(
            @NonNull Long id
    ) {


        return schemeRepository.findById(
                Objects.requireNonNull(id)
        )
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Scheme not found with ID: " + id
                )
        );

    }





    public Scheme saveScheme(
            Scheme scheme
    ) {

        Objects.requireNonNull(
                scheme,
                "Scheme cannot be null"
        );

        validateCriteriaOrdering(scheme);

        return schemeRepository.save(scheme);

    }





    public Scheme updateScheme(
            @NonNull Long id,
            Scheme schemeDetails
    ) {


        Objects.requireNonNull(
                schemeDetails,
                "Scheme details cannot be null"
        );



        Scheme scheme =
                schemeRepository.findById(
                        Objects.requireNonNull(id)
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Scheme not found with ID: " + id
                        )
                );



        scheme.setSchemeCode(
                schemeDetails.getSchemeCode()
        );


        scheme.setName(
                schemeDetails.getName()
        );


        scheme.setDescription(
                schemeDetails.getDescription()
        );


        scheme.setSchemeType(
                schemeDetails.getSchemeType()
        );


        scheme.setDisbursementMode(
                schemeDetails.getDisbursementMode()
        );


        scheme.setFrequency(
                schemeDetails.getFrequency()
        );


        scheme.setAmount(
                schemeDetails.getAmount()
        );


        scheme.setMaxBeneficiaries(
                schemeDetails.getMaxBeneficiaries()
        );


        // Eligibility criteria (Module 2 requirement: "configurable scheme
        // criteria") — these are the two fields that actually exist on the
        // Scheme entity. There is no minAge/maxAge/eligibleCategory/
        // minGrantAmount/maxGrantAmount/regionalBudgetAllocation on this
        // entity; earlier code referenced those and did not compile.
        scheme.setMaxAnnualIncome(
                schemeDetails.getMaxAnnualIncome()
        );


        scheme.setAllowedCategories(
                schemeDetails.getAllowedCategories()
        );


        scheme.setStartDate(
                schemeDetails.getStartDate()
        );


        scheme.setEndDate(
                schemeDetails.getEndDate()
        );


        scheme.setIsActive(
                schemeDetails.getIsActive()
        );


        validateCriteriaOrdering(scheme);


        return schemeRepository.save(scheme);

    }







    /**
     * Server-side guard on Scheme's date range. The frontend already blocks
     * start > end in the Add/Edit Scheme dialog, but that's bypassable via
     * a direct API call — and an inverted date range would make a scheme
     * appear expired the moment it's created.
     */
    private void validateCriteriaOrdering(Scheme scheme) {

        if (scheme.getStartDate() != null
                &&
            scheme.getEndDate() != null
                &&
            scheme.getStartDate().isAfter(scheme.getEndDate())) {

            throw new IllegalArgumentException(
                    "startDate cannot be after endDate"
            );

        }

    }




    public void deleteScheme(
            @NonNull Long id
    ) {


        if(!schemeRepository.existsById(
                Objects.requireNonNull(id)
        )) {


            throw new ResourceNotFoundException(
                    "Scheme not found with ID: " + id
            );

        }



        schemeRepository.deleteById(
                Objects.requireNonNull(id)
        );

    }

}