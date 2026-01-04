package com.realestate.identity.controller;

import com.realestate.identity.dto.CountryDTO;
import com.realestate.identity.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/identity/countries")
@Tag(name = "Countries", description = "API pour la gestion des pays (SaaS Configuration)")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    @Operation(summary = "Récupère tous les pays actifs", description = "Retourne la liste de tous les pays actifs, triés par ordre d'affichage puis par nom")
    public ResponseEntity<List<CountryDTO>> getAllActiveCountries() {
        List<CountryDTO> countries = countryService.getAllActiveCountries();
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère un pays par son ID", description = "Retourne les détails d'un pays avec ses villes")
    public ResponseEntity<CountryDTO> getCountryById(@PathVariable Long id) {
        CountryDTO country = countryService.getCountryById(id);
        return ResponseEntity.ok(country);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Récupère un pays par son code", description = "Retourne les détails d'un pays par son code ISO (ex: FR, US)")
    public ResponseEntity<CountryDTO> getCountryByCode(@PathVariable String code) {
        CountryDTO country = countryService.getCountryByCode(code);
        return ResponseEntity.ok(country);
    }

    @PostMapping
    @Operation(summary = "Crée un nouveau pays", description = "Crée un nouveau pays (nécessite les permissions ADMIN)")
    public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryDTO countryDTO) {
        CountryDTO created = countryService.createCountry(countryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Met à jour un pays", description = "Met à jour les informations d'un pays (nécessite les permissions ADMIN)")
    public ResponseEntity<CountryDTO> updateCountry(@PathVariable Long id, @Valid @RequestBody CountryDTO countryDTO) {
        CountryDTO updated = countryService.updateCountry(id, countryDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un pays", description = "Supprime un pays (nécessite les permissions ADMIN). Ne peut pas supprimer un pays avec des villes associées")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
}

