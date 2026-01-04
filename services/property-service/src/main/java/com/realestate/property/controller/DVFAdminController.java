package com.realestate.property.controller;

import com.realestate.property.dto.DVFImportHistoryDTO;
import com.realestate.property.dto.DVFStatsDTO;
import com.realestate.property.entity.DVFImportHistory;
import com.realestate.property.repository.DVFImportHistoryRepository;
import com.realestate.property.service.DVFService;
import com.realestate.property.service.DVFStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/admin/dvf")
@Tag(name = "DVF Admin", description = "API d'administration pour la synchronisation des données DVF")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class DVFAdminController {

    private final DVFService dvfService;
    private final DVFStatsService dvfStatsService;
    private final DVFImportHistoryRepository importHistoryRepository;

    public DVFAdminController(
            DVFService dvfService,
            DVFStatsService dvfStatsService,
            DVFImportHistoryRepository importHistoryRepository) {
        this.dvfService = dvfService;
        this.dvfStatsService = dvfStatsService;
        this.importHistoryRepository = importHistoryRepository;
    }

    @PostMapping("/import/{year}/{department}")
    @Operation(summary = "Importer les données DVF pour une année et un département",
               description = "Déclenche manuellement l'importation des données DVF depuis data.gouv.fr")
    public ResponseEntity<Map<String, Object>> importDVFData(
            @PathVariable String year,
            @PathVariable String department) {
        
        Long userId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "started");
        response.put("year", year);
        response.put("department", department);
        response.put("message", "Import started in background");

        CompletableFuture<Integer> future = dvfService.importDVFData(year, department, userId);
        future.thenAccept(count -> {
            response.put("status", "completed");
            response.put("importedCount", count);
        }).exceptionally(e -> {
            response.put("status", "error");
            response.put("error", e.getMessage());
            return null;
        });

        return ResponseEntity.accepted().body(response);
    }

    @DeleteMapping("/clean/{year}")
    @Operation(summary = "Nettoyer les données DVF pour une année",
               description = "Supprime toutes les transactions DVF pour une année donnée")
    public ResponseEntity<Map<String, String>> cleanDVFData(@PathVariable String year) {
        dvfService.cleanOldData(year);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Old DVF data cleaned for year " + year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Récupérer l'historique des imports",
               description = "Retourne l'historique des imports DVF avec pagination")
    public ResponseEntity<org.springframework.data.domain.Page<DVFImportHistoryDTO>> getImportHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        org.springframework.data.domain.Page<DVFImportHistory> historyPage = importHistoryRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        org.springframework.data.domain.Page<DVFImportHistoryDTO> dtoPage = historyPage.map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/stats")
    @Operation(summary = "Récupérer les statistiques globales DVF",
               description = "Retourne les statistiques globales sur les données DVF")
    public ResponseEntity<DVFStatsDTO> getStats() {
        DVFStatsDTO stats = dvfStatsService.getGlobalStats();
        return ResponseEntity.ok(stats);
    }

    private DVFImportHistoryDTO toDTO(DVFImportHistory history) {
        DVFImportHistoryDTO dto = new DVFImportHistoryDTO();
        dto.setId(history.getId());
        dto.setYear(history.getYear());
        dto.setDepartment(history.getDepartment());
        dto.setStatus(history.getStatus());
        dto.setTransactionCount(history.getTransactionCount());
        dto.setErrorMessage(history.getErrorMessage());
        dto.setStartedBy(history.getStartedBy());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setUpdatedAt(history.getUpdatedAt());
        dto.setCompletedAt(history.getCompletedAt());
        return dto;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // TODO: Implémenter l'extraction de l'ID utilisateur depuis le token JWT
            return null;
        }
        return null;
    }
}

