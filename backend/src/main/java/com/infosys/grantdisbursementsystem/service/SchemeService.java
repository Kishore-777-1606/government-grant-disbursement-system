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


        scheme.setStartDate(
                schemeDetails.getStartDate()
        );


        scheme.setEndDate(
                schemeDetails.getEndDate()
        );


        scheme.setIsActive(
                schemeDetails.getIsActive()
        );



        return schemeRepository.save(scheme);

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