package com.realestate.property.service;

import com.realestate.property.dto.PriceAlertCreateDTO;
import com.realestate.property.dto.PriceAlertDTO;
import com.realestate.property.entity.PriceAlert;
import com.realestate.property.entity.Property;
import com.realestate.property.mapper.PriceAlertMapper;
import com.realestate.property.repository.PriceAlertRepository;
import com.realestate.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PriceAlertService {

    private static final Logger logger = LoggerFactory.getLogger(PriceAlertService.class);

    private final PriceAlertRepository priceAlertRepository;
    private final PropertyRepository propertyRepository;
    private final PriceAlertMapper priceAlertMapper;

    public PriceAlertService(
            PriceAlertRepository priceAlertRepository,
            PropertyRepository propertyRepository,
            PriceAlertMapper priceAlertMapper) {
        this.priceAlertRepository = priceAlertRepository;
        this.propertyRepository = propertyRepository;
        this.priceAlertMapper = priceAlertMapper;
    }

    /**
     * Créer une nouvelle alerte de prix
     */
    @Transactional
    public PriceAlertDTO createPriceAlert(PriceAlertCreateDTO createDTO, Long userId) {
        logger.debug("Creating price alert for property ID: {} by user ID: {}", createDTO.getPropertyId(), userId);

        // Vérifier que la propriété existe
        Property property = propertyRepository.findById(createDTO.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + createDTO.getPropertyId()));

        // Vérifier si une alerte existe déjà pour cette propriété et cet utilisateur
        Optional<PriceAlert> existingAlert = priceAlertRepository
                .findByPropertyIdAndUserIdAndActiveTrue(createDTO.getPropertyId(), userId);
        
        if (existingAlert.isPresent()) {
            throw new IllegalArgumentException("Une alerte existe déjà pour cette propriété. Veuillez la modifier ou la supprimer d'abord.");
        }

        // Valider les paramètres selon le type d'alerte
        validateAlertParameters(createDTO, property.getPrice());

        PriceAlert priceAlert = priceAlertMapper.toEntity(createDTO);
        priceAlert.setUserId(userId);

        PriceAlert saved = priceAlertRepository.save(priceAlert);
        logger.info("Price alert created with ID: {} for property ID: {}", saved.getId(), createDTO.getPropertyId());

        return enrichDTO(priceAlertMapper.toDTO(saved));
    }

    /**
     * Valider les paramètres de l'alerte
     */
    private void validateAlertParameters(PriceAlertCreateDTO createDTO, BigDecimal currentPrice) {
        String alertType = createDTO.getAlertType();
        
        if (alertType == null) {
            throw new IllegalArgumentException("Le type d'alerte est requis");
        }

        switch (alertType) {
            case "PRICE_DROP":
                if (createDTO.getTargetPrice() == null) {
                    throw new IllegalArgumentException("Le prix cible est requis pour une alerte de baisse de prix");
                }
                if (createDTO.getTargetPrice().compareTo(currentPrice) >= 0) {
                    throw new IllegalArgumentException("Le prix cible doit être inférieur au prix actuel");
                }
                break;
            case "PRICE_INCREASE":
                if (createDTO.getTargetPrice() == null) {
                    throw new IllegalArgumentException("Le prix cible est requis pour une alerte de hausse de prix");
                }
                if (createDTO.getTargetPrice().compareTo(currentPrice) <= 0) {
                    throw new IllegalArgumentException("Le prix cible doit être supérieur au prix actuel");
                }
                break;
            case "PERCENTAGE_DROP":
                if (createDTO.getPercentageThreshold() == null || createDTO.getPercentageThreshold() <= 0) {
                    throw new IllegalArgumentException("Le seuil de pourcentage est requis et doit être positif");
                }
                break;
            case "PERCENTAGE_INCREASE":
                if (createDTO.getPercentageThreshold() == null || createDTO.getPercentageThreshold() <= 0) {
                    throw new IllegalArgumentException("Le seuil de pourcentage est requis et doit être positif");
                }
                break;
            default:
                throw new IllegalArgumentException("Type d'alerte invalide: " + alertType);
        }
    }

    /**
     * Vérifier et déclencher les alertes pour une propriété après un changement de prix
     */
    @Transactional
    public List<PriceAlertDTO> checkAndTriggerAlerts(Long propertyId, BigDecimal newPrice, BigDecimal oldPrice) {
        logger.debug("Checking alerts for property ID: {} with new price: {}", propertyId, newPrice);

        List<PriceAlert> activeAlerts = priceAlertRepository.findActiveUnnotifiedAlertsByPropertyId(propertyId);
        List<PriceAlert> triggeredAlerts = activeAlerts.stream()
                .filter(alert -> shouldTriggerAlert(alert, newPrice, oldPrice))
                .collect(Collectors.toList());

        for (PriceAlert alert : triggeredAlerts) {
            alert.setNotified(true);
            alert.setNotifiedAt(LocalDateTime.now());
            priceAlertRepository.save(alert);
            logger.info("Price alert triggered: ID {} for property ID: {}", alert.getId(), propertyId);
        }

        return triggeredAlerts.stream()
                .map(priceAlertMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Vérifier si une alerte doit être déclenchée
     */
    private boolean shouldTriggerAlert(PriceAlert alert, BigDecimal newPrice, BigDecimal oldPrice) {
        if (oldPrice == null || oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return false; // Pas de prix précédent pour comparer
        }

        String alertType = alert.getAlertType();
        BigDecimal priceChange = newPrice.subtract(oldPrice);
        double percentageChange = priceChange.divide(oldPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        switch (alertType) {
            case "PRICE_DROP":
                return newPrice.compareTo(alert.getTargetPrice()) <= 0;
            case "PRICE_INCREASE":
                return newPrice.compareTo(alert.getTargetPrice()) >= 0;
            case "PERCENTAGE_DROP":
                return percentageChange <= -alert.getPercentageThreshold();
            case "PERCENTAGE_INCREASE":
                return percentageChange >= alert.getPercentageThreshold();
            default:
                return false;
        }
    }

    /**
     * Récupérer toutes les alertes actives pour un utilisateur
     */
    @Transactional(readOnly = true)
    public List<PriceAlertDTO> getAlertsByUserId(Long userId) {
        logger.debug("Fetching alerts for user ID: {}", userId);
        return priceAlertRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(priceAlertMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les alertes actives pour une propriété
     */
    @Transactional(readOnly = true)
    public List<PriceAlertDTO> getAlertsByPropertyId(Long propertyId) {
        logger.debug("Fetching alerts for property ID: {}", propertyId);
        return priceAlertRepository.findByPropertyIdAndActiveTrue(propertyId).stream()
                .map(priceAlertMapper::toDTO)
                .map(this::enrichDTO)
                .collect(Collectors.toList());
    }

    /**
     * Désactiver une alerte
     */
    @Transactional
    public void deactivateAlert(Long alertId) {
        logger.debug("Deactivating alert ID: {}", alertId);
        PriceAlert alert = priceAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with ID: " + alertId));
        alert.setActive(false);
        priceAlertRepository.save(alert);
        logger.info("Alert deactivated: ID {}", alertId);
    }

    /**
     * Supprimer une alerte
     */
    @Transactional
    public void deleteAlert(Long alertId) {
        logger.debug("Deleting alert ID: {}", alertId);
        priceAlertRepository.deleteById(alertId);
        logger.info("Alert deleted: ID {}", alertId);
    }

    /**
     * Enrichir le DTO avec les informations de la propriété
     */
    private PriceAlertDTO enrichDTO(PriceAlertDTO dto) {
        if (dto == null || dto.getPropertyId() == null) {
            return dto;
        }
        
        propertyRepository.findById(dto.getPropertyId()).ifPresent(property -> {
            dto.setPropertyTitle(property.getTitle());
        });
        
        return dto;
    }
}

