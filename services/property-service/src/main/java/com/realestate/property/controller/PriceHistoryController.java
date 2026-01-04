package com.realestate.property.controller;

import com.realestate.property.dto.PriceHistoryCreateDTO;
import com.realestate.property.dto.PriceHistoryDTO;
import com.realestate.property.dto.PriceHistoryStatsDTO;
import com.realestate.property.service.PriceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Price History", description = "Price history management API")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @PostMapping("/{propertyId}/price-history")
    @Operation(summary = "Create price history entry", description = "Creates a new price history entry for a property")
    public ResponseEntity<PriceHistoryDTO> createPriceHistory(
            @PathVariable Long propertyId,
            @Valid @RequestBody PriceHistoryCreateDTO createDTO) {
        // S'assurer que le propertyId dans le DTO correspond au path
        createDTO.setPropertyId(propertyId);
        
        // Récupérer l'ID de l'utilisateur connecté
        Long userId = getCurrentUserId();
        
        PriceHistoryDTO created = priceHistoryService.createPriceHistory(createDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{propertyId}/price-history")
    @Operation(summary = "Get price history", description = "Returns the price history for a property")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistory(
            @PathVariable Long propertyId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        
        if (size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<PriceHistoryDTO> historyPage = priceHistoryService.getPriceHistoryByPropertyId(propertyId, pageable);
            return ResponseEntity.ok(historyPage.getContent());
        } else {
            List<PriceHistoryDTO> history = priceHistoryService.getPriceHistoryByPropertyId(propertyId);
            return ResponseEntity.ok(history);
        }
    }

    @GetMapping("/{propertyId}/price-history/stats")
    @Operation(summary = "Get price history statistics", description = "Returns statistics about the price history for a property")
    public ResponseEntity<PriceHistoryStatsDTO> getPriceHistoryStats(@PathVariable Long propertyId) {
        PriceHistoryStatsDTO stats = priceHistoryService.getPriceHistoryStats(propertyId);
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/price-history/{id}")
    @Operation(summary = "Delete price history entry", description = "Deletes a price history entry")
    public ResponseEntity<Void> deletePriceHistory(@PathVariable Long id) {
        priceHistoryService.deletePriceHistory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer l'ID de l'utilisateur actuellement connecté
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Si l'utilisateur est authentifié, on peut extraire l'ID depuis les claims JWT ou depuis le UserDetails
            // Pour l'instant, on retourne null si on ne peut pas l'extraire
            // TODO: Implémenter l'extraction de l'ID utilisateur depuis le token JWT
            return null;
        }
        return null;
    }
}

