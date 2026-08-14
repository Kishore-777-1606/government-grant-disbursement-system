package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.entity.Scheme;
import com.infosys.grantdisbursementsystem.service.SchemeService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;



@RestController
@RequestMapping("/api/schemes")
public class SchemeController {



    private final SchemeService schemeService;



    public SchemeController(
            SchemeService schemeService
    ) {

        this.schemeService = schemeService;

    }





    @GetMapping
    public List<Scheme> getAllSchemes() {

        return schemeService.getAllSchemes();

    }





    @GetMapping("/{id}")
    public Scheme getSchemeById(
            @PathVariable Long id
    ) {


        return schemeService.getSchemeById(
                Objects.requireNonNull(id)
        );

    }




@PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'ADMIN')")
    @PostMapping
    public Scheme createScheme(
            @RequestBody Scheme scheme
    ) {


        return schemeService.saveScheme(
                scheme
        );

    }





  @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id}")
    public Scheme updateScheme(
            @PathVariable Long id,
            @RequestBody Scheme schemeDetails
    ) {


        return schemeService.updateScheme(
                Objects.requireNonNull(id),
                schemeDetails
        );

    }





  @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteScheme(
            @PathVariable Long id
    ) {


        schemeService.deleteScheme(
                Objects.requireNonNull(id)
        );


        return "Scheme deleted successfully";

    }

}