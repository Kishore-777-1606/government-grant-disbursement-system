package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.entity.Scheme;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class EligibilityScoringService {


    public static final double BENEFICIARY_EXISTS_WEIGHT = 30;
    public static final double AADHAAR_VERIFIED_WEIGHT = 20;
    public static final double BANK_VERIFIED_WEIGHT = 20;
    public static final double ACTIVE_SCHEME_WEIGHT = 30;
    public static final double ELIGIBLE_THRESHOLD = 70;


    public double calculateScore(Beneficiary beneficiary, Scheme scheme) {

        double score = 0;

        if(beneficiary != null) {
            score += BENEFICIARY_EXISTS_WEIGHT;

            if(Boolean.TRUE.equals(beneficiary.getAadhaarVerified())) {
                score += AADHAAR_VERIFIED_WEIGHT;
            }

            if(Boolean.TRUE.equals(beneficiary.getBankVerified())) {
                score += BANK_VERIFIED_WEIGHT;
            }
        }

        if(scheme != null && Boolean.TRUE.equals(scheme.getIsActive())) {
            score += ACTIVE_SCHEME_WEIGHT;
        }

        return score;
    }


    /**
     * Hard eligibility gate driven by the scheme's own configured criteria
     * (Module 2 requirement: "configurable scheme criteria" / "grant amount
     * slabs"). Unlike calculateScore() - which measures data-quality signals
     * like Aadhaar/bank verification - this checks whether the beneficiary
     * actually QUALIFIES for this specific scheme at all. A beneficiary can
     * score highly on data quality and still fail this gate (e.g. income
     * above the scheme's ceiling, or wrong category) - in which case the
     * application must not be marked eligible regardless of score.
     */
    public boolean meetsSchemeCriteria(Beneficiary beneficiary, Scheme scheme) {

        if(beneficiary == null || scheme == null) {
            return false;
        }

        // Category check - only enforced if the scheme restricts categories
        String allowedCategories = scheme.getAllowedCategories();

        if(allowedCategories != null && !allowedCategories.isBlank()) {

            String category = beneficiary.getCategory();

            if(category == null || category.isBlank()) {
                return false;
            }

            List<String> allowed = Arrays.stream(allowedCategories.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            if(!allowed.contains(category.trim().toUpperCase())) {
                return false;
            }
        }

        // Income ceiling check - only enforced if the scheme sets one
        if(scheme.getMaxAnnualIncome() != null) {

            if(beneficiary.getAnnualIncome() == null) {
                return false;
            }

            if(beneficiary.getAnnualIncome().compareTo(scheme.getMaxAnnualIncome()) > 0) {
                return false;
            }
        }

        return true;
    }


    public boolean isEligible(double score) {
        return score >= ELIGIBLE_THRESHOLD;
    }


    
    public boolean isEligible(double score, Beneficiary beneficiary, Scheme scheme) {
        return isEligible(score) && meetsSchemeCriteria(beneficiary, scheme);
    }

}