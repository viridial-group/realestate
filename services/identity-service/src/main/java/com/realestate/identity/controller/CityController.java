package com.realestate.identity.controller;

import com.realestate.identity.dto.CityDTO;
import com.realestate.identity.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/identity/cities")
@Tag(name = "Cities", description = "API pour la gestion des villes (SaaS Configuration)")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(summary = "Récupère toutes les villes actives", description = "Retourne la liste de toutes les villes actives, triées par pays puis par ordre d'affichage")
    public ResponseEntity<List<CityDTO>> getAllActiveCities() {
        List<CityDTO> cities = cityService.getAllActiveCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/country/{countryCode}")
    @Operation(summary = "Récupère les villes d'un pays", description = "Retourne la liste des villes actives d'un pays par son code (ex: FR, US)")
    public ResponseEntity<List<CityDTO>> getCitiesByCountryCode(@PathVariable String countryCode) {
        List<CityDTO> cities = cityService.getCitiesByCountryCode(countryCode);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/country-id/{countryId}")
    @Operation(summary = "Récupère les villes d'un pays par ID", description = "Retourne la liste des villes actives d'un pays par son ID")
    public ResponseEntity<List<CityDTO>> getCitiesByCountryId(@PathVariable Long countryId) {
        List<CityDTO> cities = cityService.getCitiesByCountryId(countryId);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère une ville par son ID", description = "Retourne les détails d'une ville")
    public ResponseEntity<CityDTO> getCityById(@PathVariable Long id) {
        CityDTO city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    @PostMapping
    @Operation(summary = "Crée une nouvelle ville", description = "Crée une nouvelle ville (nécessite les permissions ADMIN)")
    public ResponseEntity<CityDTO> createCity(@Valid @RequestBody CityDTO cityDTO) {
        CityDTO created = cityService.createCity(cityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Met à jour une ville", description = "Met à jour les informations d'une ville (nécessite les permissions ADMIN)")
    public ResponseEntity<CityDTO> updateCity(@PathVariable Long id, @Valid @RequestBody CityDTO cityDTO) {
        CityDTO updated = cityService.updateCity(id, cityDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une ville", description = "Supprime une ville (nécessite les permissions ADMIN)")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}

