package com.realestate.property.controller;

import com.realestate.property.dto.PriceAlertCreateDTO;
import com.realestate.property.dto.PriceAlertDTO;
import com.realestate.property.service.PriceAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-alerts")
@Tag(name = "Price Alerts", description = "Price alert management API")
public class PriceAlertController {

    private final PriceAlertService priceAlertService;

    public PriceAlertController(PriceAlertService priceAlertService) {
        this.priceAlertService = priceAlertService;
    }

    @PostMapping
    @Operation(summary = "Create price alert", description = "Creates a new price alert for a property")
    public ResponseEntity<PriceAlertDTO> createPriceAlert(@Valid @RequestBody PriceAlertCreateDTO createDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        PriceAlertDTO created = priceAlertService.createPriceAlert(createDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my-alerts")
    @Operation(summary = "Get my price alerts", description = "Returns all active price alerts for the current user")
    public ResponseEntity<List<PriceAlertDTO>> getMyAlerts() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<PriceAlertDTO> alerts = priceAlertService.getAlertsByUserId(userId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get alerts by property", description = "Returns all active price alerts for a property")
    public ResponseEntity<List<PriceAlertDTO>> getAlertsByProperty(@PathVariable Long propertyId) {
        List<PriceAlertDTO> alerts = priceAlertService.getAlertsByPropertyId(propertyId);
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate price alert", description = "Deactivates a price alert")
    public ResponseEntity<Void> deactivateAlert(@PathVariable Long id) {
        priceAlertService.deactivateAlert(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete price alert", description = "Deletes a price alert")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        priceAlertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer l'ID de l'utilisateur actuellement connecté
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // TODO: Implémenter l'extraction de l'ID utilisateur depuis le token JWT
            return null;
        }
        return null;
    }
}

