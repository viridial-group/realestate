package com.realestate.property.service;

import com.realestate.property.dto.DVFStatsDTO;
import com.realestate.property.repository.DVFTransactionRepository;
import com.realestate.property.repository.DVFImportHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour les statistiques globales DVF
 */
@Service
@Transactional(readOnly = true)
public class DVFStatsService {

    private final DVFTransactionRepository dvfTransactionRepository;
    private final DVFImportHistoryRepository importHistoryRepository;

    public DVFStatsService(
            DVFTransactionRepository dvfTransactionRepository,
            DVFImportHistoryRepository importHistoryRepository) {
        this.dvfTransactionRepository = dvfTransactionRepository;
        this.importHistoryRepository = importHistoryRepository;
    }

    /**
     * Obtient les statistiques globales DVF
     */
    public DVFStatsDTO getGlobalStats() {
        DVFStatsDTO stats = new DVFStatsDTO();

        // Nombre total de transactions
        stats.setTotalTransactions(dvfTransactionRepository.count());

        // Départements couverts
        stats.setCoveredDepartments(importHistoryRepository.countDistinctDepartments());

        // Années disponibles
        stats.setAvailableYears(importHistoryRepository.findDistinctYears());

        // Prix moyen et médian au m² (toutes zones confondues)
        List<Object[]> priceStats = dvfTransactionRepository.findAll()
            .stream()
            .filter(t -> t.getPrixMetreCarre() != null && t.getPrixMetreCarre().compareTo(BigDecimal.ZERO) > 0)
            .map(t -> new Object[]{t.getPrixMetreCarre()})
            .collect(Collectors.toList());

        if (!priceStats.isEmpty()) {
            BigDecimal sum = priceStats.stream()
                .map(o -> (BigDecimal) o[0])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAveragePricePerSquareMeter(
                sum.divide(BigDecimal.valueOf(priceStats.size()), 2, java.math.RoundingMode.HALF_UP)
            );

            // Médiane (simplifié - pour une vraie médiane, utiliser une requête SQL)
            List<BigDecimal> sortedPrices = priceStats.stream()
                .map(o -> (BigDecimal) o[0])
                .sorted()
                .collect(Collectors.toList());
            int medianIndex = sortedPrices.size() / 2;
            stats.setMedianPricePerSquareMeter(sortedPrices.get(medianIndex));
        }

        // Dernière mise à jour
        importHistoryRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 1))
            .getContent()
            .stream()
            .findFirst()
            .ifPresent(history -> stats.setLastUpdate(history.getUpdatedAt()));

        // Imports terminés et en cours
        stats.setCompletedImports(importHistoryRepository.countByStatus("TERMINÉ"));
        stats.setInProgressImports(importHistoryRepository.countByStatus("EN_COURS"));

        return stats;
    }
}

