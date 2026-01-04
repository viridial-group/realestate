package com.realestate.property.controller;

import com.realestate.property.dto.DashboardStatsDTO;
import com.realestate.property.dto.StatsHistoryPointDTO;
import com.realestate.property.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/history")
    @Operation(
            summary = "Get global statistics history",
            description = "Returns statistics history for all properties over a specified number of days. " +
                          "Cached for 5 minutes. Default: 7 days, max: 90 days."
    )
    public ResponseEntity<List<StatsHistoryPointDTO>> getGlobalStatsHistory(
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        List<StatsHistoryPointDTO> history = statsService.getGlobalStatsHistory(days);
        return ResponseEntity.ok(history);
    }
}

