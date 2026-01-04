package com.realestate.property.service;

import com.realestate.property.dto.PriceHistoryCreateDTO;
import com.realestate.property.dto.PriceHistoryDTO;
import com.realestate.property.dto.PriceHistoryStatsDTO;
import com.realestate.property.entity.PriceHistory;
import com.realestate.property.entity.Property;
import com.realestate.property.mapper.PriceHistoryMapper;
import com.realestate.property.repository.PriceHistoryRepository;
import com.realestate.property.repository.PropertyRepository;
import com.realestate.property.service.PriceAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(PriceHistoryService.class);

    private final PriceHistoryRepository priceHistoryRepository;
    private final PropertyRepository propertyRepository;
    private final PriceHistoryMapper priceHistoryMapper;

    public PriceHistoryService(
            PriceHistoryRepository priceHistoryRepository,
            PropertyRepository propertyRepository,
            PriceHistoryMapper priceHistoryMapper) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.propertyRepository = propertyRepository;
        this.priceHistoryMapper = priceHistoryMapper;
    }

    /**
     * Créer une nouvelle entrée d'historique de prix
     */
    @Transactional
    public PriceHistoryDTO createPriceHistory(PriceHistoryCreateDTO createDTO, Long userId) {
        logger.debug("Creating price history for property ID: {}", createDTO.getPropertyId());

        // Vérifier que la propriété existe
        Property property = propertyRepository.findById(createDTO.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + createDTO.getPropertyId()));

        // Vérifier si le prix a vraiment changé
        PriceHistory lastHistory = priceHistoryRepository
                .findFirstByPropertyIdOrderByChangeDateDesc(createDTO.getPropertyId())
                .orElse(null);

        if (lastHistory != null && lastHistory.getPrice().compareTo(createDTO.getPrice()) == 0) {
            logger.warn("Price unchanged for property ID: {}. Skipping history entry.", createDTO.getPropertyId());
            throw new IllegalArgumentException("Le prix n'a pas changé. Aucune entrée d'historique créée.");
        }

        PriceHistory priceHistory = priceHistoryMapper.toEntity(createDTO);
        priceHistory.setCreatedBy(userId);
        
        // Si changeDate n'est pas fourni, utiliser maintenant
        if (priceHistory.getChangeDate() == null) {
            priceHistory.setChangeDate(LocalDateTime.now());
        }

        PriceHistory saved = priceHistoryRepository.save(priceHistory);

        // Mettre à jour le prix de la propriété
        property.setPrice(createDTO.getPrice());
        if (createDTO.getCurrency() != null) {
            property.setCurrency(createDTO.getCurrency());
        }
        propertyRepository.save(property);

        logger.info("Price history created for property ID: {} with price: {}", 
                createDTO.getPropertyId(), createDTO.getPrice());

        // Vérifier et déclencher les alertes de prix
        try {
            if (priceAlertService != null) {
                // Récupérer le prix précédent depuis l'historique (on vient d'en créer un, donc on prend le suivant)
                List<PriceHistory> allHistory = priceHistoryRepository.findByPropertyIdOrderByChangeDateDesc(createDTO.getPropertyId());
                BigDecimal oldPrice = null;
                if (allHistory.size() > 1) {
                    oldPrice = allHistory.get(1).getPrice(); // Le deuxième élément (le précédent)
                } else {
                    // Si c'est le premier historique, utiliser le prix actuel de la propriété comme référence
                    oldPrice = property.getPrice();
                }
                priceAlertService.checkAndTriggerAlerts(createDTO.getPropertyId(), createDTO.getPrice(), oldPrice);
            }
        } catch (Exception e) {
            logger.warn("Failed to check price alerts for property {}: {}", createDTO.getPropertyId(), e.getMessage());
            // Ne pas bloquer la création de l'historique si les alertes échouent
        }

        return priceHistoryMapper.toDTO(saved);
    }

    @Autowired(required = false)
    private PriceAlertService priceAlertService;

    /**
     * Récupérer l'historique des prix pour une propriété
     */
    @Transactional(readOnly = true)
    public List<PriceHistoryDTO> getPriceHistoryByPropertyId(Long propertyId) {
        logger.debug("Fetching price history for property ID: {}", propertyId);
        List<PriceHistory> history = priceHistoryRepository.findByPropertyIdOrderByChangeDateDesc(propertyId);
        return history.stream()
                .map(priceHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer l'historique des prix pour une propriété avec pagination
     */
    @Transactional(readOnly = true)
    public Page<PriceHistoryDTO> getPriceHistoryByPropertyId(Long propertyId, Pageable pageable) {
        logger.debug("Fetching price history for property ID: {} with pagination", propertyId);
        return priceHistoryRepository.findByPropertyIdOrderByChangeDateDesc(propertyId, pageable)
                .map(priceHistoryMapper::toDTO);
    }

    /**
     * Récupérer les statistiques sur l'historique des prix
     */
    @Transactional(readOnly = true)
    public PriceHistoryStatsDTO getPriceHistoryStats(Long propertyId) {
        logger.debug("Fetching price history stats for property ID: {}", propertyId);

        List<PriceHistory> history = priceHistoryRepository.findByPropertyIdOrderByChangeDateDesc(propertyId);
        
        if (history.isEmpty()) {
            // Si pas d'historique, récupérer le prix actuel de la propriété
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));
            
            PriceHistoryStatsDTO stats = new PriceHistoryStatsDTO();
            stats.setCurrentPrice(property.getPrice());
            stats.setInitialPrice(property.getPrice());
            stats.setMinPrice(property.getPrice());
            stats.setMaxPrice(property.getPrice());
            stats.setTotalChange(BigDecimal.ZERO);
            stats.setTotalChangePercent(0.0);
            stats.setTotalChanges(0L);
            stats.setTrend("STABLE");
            return stats;
        }

        PriceHistoryStatsDTO stats = new PriceHistoryStatsDTO();

        // Prix actuel (le plus récent)
        PriceHistory current = history.get(0);
        stats.setCurrentPrice(current.getPrice());
        stats.setLastPriceDate(current.getChangeDate());

        // Prix initial (le plus ancien)
        PriceHistory initial = history.get(history.size() - 1);
        stats.setInitialPrice(initial.getPrice());
        stats.setFirstPriceDate(initial.getChangeDate());

        // Calculer min et max
        BigDecimal minPrice = history.stream()
                .map(PriceHistory::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(current.getPrice());
        BigDecimal maxPrice = history.stream()
                .map(PriceHistory::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(current.getPrice());

        stats.setMinPrice(minPrice);
        stats.setMaxPrice(maxPrice);

        // Calculer la variation totale
        BigDecimal totalChange = current.getPrice().subtract(initial.getPrice());
        stats.setTotalChange(totalChange);

        // Calculer le pourcentage de variation
        if (initial.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            double changePercent = totalChange.divide(initial.getPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            stats.setTotalChangePercent(changePercent);
        } else {
            stats.setTotalChangePercent(0.0);
        }

        // Nombre total de changements
        stats.setTotalChanges((long) history.size());

        // Calculer la durée depuis le premier prix
        if (stats.getFirstPriceDate() != null) {
            long days = ChronoUnit.DAYS.between(stats.getFirstPriceDate(), LocalDateTime.now());
            stats.setDaysSinceFirstPrice(days);
        }

        // Déterminer la tendance
        if (history.size() >= 2) {
            PriceHistory previous = history.get(1);
            int comparison = current.getPrice().compareTo(previous.getPrice());
            if (comparison > 0) {
                stats.setTrend("UP");
            } else if (comparison < 0) {
                stats.setTrend("DOWN");
            } else {
                stats.setTrend("STABLE");
            }
        } else {
            stats.setTrend("STABLE");
        }

        return stats;
    }

    /**
     * Supprimer une entrée d'historique
     */
    @Transactional
    public void deletePriceHistory(Long id) {
        logger.debug("Deleting price history with ID: {}", id);
        priceHistoryRepository.deleteById(id);
        logger.info("Price history deleted with ID: {}", id);
    }
}

