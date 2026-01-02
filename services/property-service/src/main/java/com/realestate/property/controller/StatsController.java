package com.realestate.property.controller;

import com.realestate.property.dto.DashboardStatsDTO;
import com.realestate.property.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur pour les statistiques optimisées
 * Utilise des requêtes SQL natives pour performance maximale
 */
@RestController
@RequestMapping("/api/properties/stats")
@Tag(name = "Statistics", description = "Optimized statistics API with native SQL queries")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/dashboard")
    @Operation(
            summary = "Get complete dashboard statistics",
            description = "Returns comprehensive dashboard statistics using optimized native SQL queries. " +
                          "Includes property stats, user stats, and organization stats in a single response."
    )
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = statsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}

