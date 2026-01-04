package com.realestate.property.controller;

import com.realestate.property.dto.MarketDataDTO;
import com.realestate.property.entity.DVFTransaction;
import com.realestate.property.service.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/market-data")
@Tag(name = "Market Data", description = "API pour les données de marché immobilier basées sur DVF")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/postal-code/{codePostal}")
    @Operation(summary = "Obtenir les données de marché pour un code postal",
               description = "Retourne les statistiques de marché (prix moyen, médian, évolution) pour un code postal et un type de bien")
    public ResponseEntity<MarketDataDTO> getMarketDataByPostalCode(
            @PathVariable String codePostal,
            @RequestParam(required = false, defaultValue = "APARTMENT") String propertyType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusYears(2);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        MarketDataDTO marketData = marketDataService.getMarketData(codePostal, propertyType, startDate, endDate);
        return ResponseEntity.ok(marketData);
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Obtenir les données de marché pour une propriété",
               description = "Retourne les statistiques de marché avec une comparaison pour une propriété spécifique")
    public ResponseEntity<MarketDataDTO> getMarketDataForProperty(
            @PathVariable Long propertyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusYears(2);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        MarketDataDTO marketData = marketDataService.getMarketDataForProperty(propertyId, startDate, endDate);
        return ResponseEntity.ok(marketData);
    }

    @GetMapping("/property/{propertyId}/similar")
    @Operation(summary = "Trouver des transactions similaires",
               description = "Retourne les transactions DVF similaires à une propriété (même type, surface proche)")
    public ResponseEntity<List<DVFTransaction>> findSimilarTransactions(
            @PathVariable Long propertyId,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        List<DVFTransaction> transactions = marketDataService.findSimilarTransactions(propertyId, limit);
        return ResponseEntity.ok(transactions);
    }
}

