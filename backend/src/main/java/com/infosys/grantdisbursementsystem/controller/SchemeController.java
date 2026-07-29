package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Scheme;
import com.infosys.grantdisbursementsystem.service.SchemeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schemes")
public class SchemeController {

    private final SchemeService schemeService;

    public SchemeController(SchemeService schemeService) {
        this.schemeService = schemeService;
    }

    @GetMapping
    public List<Scheme> getAllSchemes() {
        return schemeService.getAllSchemes();
    }

    @GetMapping("/{id}")
    public Scheme getSchemeById(@PathVariable Long id) {
        return schemeService.getSchemeById(id);
    }

    @PostMapping
    public Scheme createScheme(@Valid @RequestBody Scheme scheme) {
        return schemeService.saveScheme(scheme);
    }

    @PutMapping("/{id}")
    public Scheme updateScheme(@PathVariable Long id,
                               @Valid @RequestBody Scheme schemeDetails) {
        return schemeService.updateScheme(id, schemeDetails);
    }

    @DeleteMapping("/{id}")
    public String deleteScheme(@PathVariable Long id) {
        schemeService.deleteScheme(id);
        return "Scheme deleted successfully!";
    }
}
