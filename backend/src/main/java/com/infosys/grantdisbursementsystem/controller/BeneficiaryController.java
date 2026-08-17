package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.service.BeneficiaryService;

import jakarta.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {


    private final BeneficiaryService beneficiaryService;


    public BeneficiaryController(
            BeneficiaryService beneficiaryService
    ) {

        this.beneficiaryService = beneficiaryService;

    }




    @GetMapping
    public List<Beneficiary> getAllBeneficiaries() {

        return beneficiaryService.getAllBeneficiaries();

    }




    @GetMapping("/{id}")
    public Beneficiary getBeneficiaryById(
            @PathVariable Long id
    ) {

        return beneficiaryService.getBeneficiaryById(
                Objects.requireNonNull(id)
        );

    }




    @PostMapping
    public Beneficiary createBeneficiary(
            @Valid @RequestBody Beneficiary beneficiary
    ) {

        return beneficiaryService.saveBeneficiary(
                beneficiary
        );

    }




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




    @DeleteMapping("/{id}")
    public String deleteBeneficiary(
            @PathVariable Long id
    ) {

        beneficiaryService.deleteBeneficiary(
                Objects.requireNonNull(id)
        );

        return "Beneficiary deleted successfully!";

    }




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