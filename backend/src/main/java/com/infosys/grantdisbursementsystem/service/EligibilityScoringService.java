package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.entity.Scheme;

import org.springframework.stereotype.Service;


@Service
public class EligibilityScoringService {


    public static final double BENEFICIARY_EXISTS_WEIGHT = 30;

    public static final double AADHAAR_VERIFIED_WEIGHT = 20;

    public static final double BANK_VERIFIED_WEIGHT = 20;

    public static final double ACTIVE_SCHEME_WEIGHT = 30;


    public static final double ELIGIBLE_THRESHOLD = 70;





    public double calculateScore(
            Beneficiary beneficiary,
            Scheme scheme
    ) {


        double score = 0;



        // Beneficiary exists
        if(beneficiary != null) {

            score += BENEFICIARY_EXISTS_WEIGHT;


            if(Boolean.TRUE.equals(
                    beneficiary.getAadhaarVerified()
            )) {

                score += AADHAAR_VERIFIED_WEIGHT;

            }



            if(Boolean.TRUE.equals(
                    beneficiary.getBankVerified()
            )) {

                score += BANK_VERIFIED_WEIGHT;

            }

        }




        // Scheme exists and active

        if(scheme != null &&
                Boolean.TRUE.equals(
                        scheme.getIsActive()
                )) {


            score += ACTIVE_SCHEME_WEIGHT;

        }



        return score;

    }






    public boolean isEligible(
            double score
    ) {


        return score >= ELIGIBLE_THRESHOLD;

    }


}