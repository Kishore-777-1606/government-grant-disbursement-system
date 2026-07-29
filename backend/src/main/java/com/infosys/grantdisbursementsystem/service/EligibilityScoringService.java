package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.entity.Scheme;
import org.springframework.stereotype.Service;

/**
 * Centralizes eligibility score calculation so the scoring rule lives in
 * exactly one place instead of being duplicated across services.
 *
 * Weights and the eligibility threshold are named constants rather than
 * magic numbers, which keeps the logic maintainable without requiring a
 * database-driven criteria table (the project's Beneficiary/Scheme schema
 * does not currently carry the income/category/disability/BPL attributes
 * that a configurable criteria table would need, so introducing one here
 * would mean redesigning those entities - out of scope for this fix).
 */
@Service
public class EligibilityScoringService {

    public static final double BENEFICIARY_EXISTS_WEIGHT = 30;
    public static final double AADHAAR_VERIFIED_WEIGHT = 20;
    public static final double BANK_VERIFIED_WEIGHT = 20;
    public static final double ACTIVE_SCHEME_WEIGHT = 30;

    public static final double ELIGIBLE_THRESHOLD = 70;

    public double calculateScore(Beneficiary beneficiary, Scheme scheme) {

        double score = 0;

        // Beneficiary record exists and was successfully resolved
        score += BENEFICIARY_EXISTS_WEIGHT;

        if (Boolean.TRUE.equals(beneficiary.getAadhaarVerified())) {
            score += AADHAAR_VERIFIED_WEIGHT;
        }

        if (Boolean.TRUE.equals(beneficiary.getBankVerified())) {
            score += BANK_VERIFIED_WEIGHT;
        }

        if (Boolean.TRUE.equals(scheme.getIsActive())) {
            score += ACTIVE_SCHEME_WEIGHT;
        }

        return score;
    }

    public boolean isEligible(double score) {
        return score >= ELIGIBLE_THRESHOLD;
    }
}
