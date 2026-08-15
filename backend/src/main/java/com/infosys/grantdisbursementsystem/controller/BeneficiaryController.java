package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.service.BeneficiaryService;

import jakarta.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService
    ) {
        this.beneficiaryService = beneficiaryService;
    }

    // Intentionally open to any authenticated role — viewing beneficiaries
    // doesn't require elevated permissions, only writes do.
    @GetMapping
    public List<Beneficiary> getAllBeneficiaries() {
        return beneficiaryService.getAllBeneficiaries();
    }

    // Intentionally open to any authenticated role — viewing beneficiaries
    // doesn't require elevated permissions, only writes do.
    @GetMapping("/{id}")
    public Beneficiary getBeneficiaryById(
            @PathVariable Long id
    ) {
        return beneficiaryService.getBeneficiaryById(
                Objects.requireNonNull(id)
        );
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'ADMIN')")
    @PostMapping
    public Beneficiary createBeneficiary(
            @Valid @RequestBody Beneficiary beneficiary
    ) {
        return beneficiaryService.saveBeneficiary(
                beneficiary
        );
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'ADMIN')")
    @PutMapping("/{id}")
    public Beneficiary updateBeneficiary(
            @PathVariable Long id,
            @Valid @RequestBody Beneficiary beneficiaryDetails
    ) {
        return beneficiaryService.updateBeneficiary(
                Objects.requireNonNull(id),
                beneficiaryDetails
        );
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id}/verification")
    public Beneficiary updateVerificationStatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Boolean> flags
    ) {
        return beneficiaryService.updateVerificationStatus(
                Objects.requireNonNull(id),
                flags.get("aadhaarVerified"),
                flags.get("bankVerified")
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteBeneficiary(
            @PathVariable Long id
    ) {
        beneficiaryService.deleteBeneficiary(
                Objects.requireNonNull(id)
        );

        return "Beneficiary deleted successfully!";
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'ADMIN')")
    @PostMapping("/{id}/document")
    public Beneficiary uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return beneficiaryService.uploadDocument(
                Objects.requireNonNull(id),
                file
        );
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'ADMIN')")
    @GetMapping("/{id}/document")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id
    ) {
        Beneficiary beneficiary = beneficiaryService.getBeneficiaryById(
                Objects.requireNonNull(id)
        );

        Resource file = beneficiaryService.loadDocument(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + beneficiary.getDocumentOriginalName() + "\""
                )
                .body(file);
    }
}