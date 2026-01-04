package com.realestate.property.service;

import com.realestate.property.dto.MarketDataDTO;
import com.realestate.property.entity.DVFTransaction;
import com.realestate.property.entity.Property;
import com.realestate.property.repository.DVFTransactionRepository;
import com.realestate.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service pour analyser les données de marché immobilier basées sur DVF
 */
@Service
@Transactional(readOnly = true)
public class MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataService.class);

    private final DVFTransactionRepository dvfTransactionRepository;
    private final PropertyRepository propertyRepository;

    public MarketDataService(
            DVFTransactionRepository dvfTransactionRepository,
            PropertyRepository propertyRepository) {
        this.dvfTransactionRepository = dvfTransactionRepository;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Obtient les données de marché pour une zone (code postal) et un type de bien
     */
    public MarketDataDTO getMarketData(String codePostal, String propertyType, LocalDate startDate, LocalDate endDate) {
        logger.debug("Getting market data for postal code: {}, type: {}, period: {} to {}", 
            codePostal, propertyType, startDate, endDate);

        // Mapper le type de propriété vers le type DVF
        String dvfType = mapPropertyTypeToDVF(propertyType);

        // Calculer les statistiques
        BigDecimal averagePrice = dvfTransactionRepository.calculateAveragePricePerSquareMeter(
            codePostal, dvfType, startDate, endDate);
        BigDecimal medianPrice = dvfTransactionRepository.calculateMedianPricePerSquareMeter(
            codePostal, dvfType, startDate, endDate);
        Long transactionCount = dvfTransactionRepository.countByCodePostalAndTypeLocalAndMutationDateBetween(
            codePostal, dvfType, startDate, endDate);

        // Obtenir les transactions pour calculer min/max
        List<DVFTransaction> transactions = dvfTransactionRepository.findByCodePostalAndTypeLocalAndMutationDateBetween(
            codePostal, dvfType, startDate, endDate, PageRequest.of(0, 1000));

        BigDecimal minPrice = transactions.stream()
            .filter(t -> t.getPrixMetreCarre() != null)
            .map(DVFTransaction::getPrixMetreCarre)
            .min(Comparator.naturalOrder())
            .orElse(null);

        BigDecimal maxPrice = transactions.stream()
            .filter(t -> t.getPrixMetreCarre() != null)
            .map(DVFTransaction::getPrixMetreCarre)
            .max(Comparator.naturalOrder())
            .orElse(null);

        // Calculer l'évolution trimestrielle
        List<MarketDataDTO.PriceEvolutionDTO> priceEvolution = calculatePriceEvolution(
            codePostal, dvfType, startDate, endDate);

        MarketDataDTO marketData = new MarketDataDTO();
        marketData.setLocation(codePostal);
        marketData.setPropertyType(propertyType);
        marketData.setAveragePricePerSquareMeter(averagePrice);
        marketData.setMedianPricePerSquareMeter(medianPrice);
        marketData.setMinPricePerSquareMeter(minPrice);
        marketData.setMaxPricePerSquareMeter(maxPrice);
        marketData.setTransactionCount(transactionCount);
        marketData.setPeriodStart(startDate);
        marketData.setPeriodEnd(endDate);
        marketData.setPriceEvolution(priceEvolution);

        return marketData;
    }

    /**
     * Obtient les données de marché pour une propriété spécifique avec comparaison
     * Uniquement pour les propriétés en France
     */
    public MarketDataDTO getMarketDataForProperty(Long propertyId, LocalDate startDate, LocalDate endDate) {
        logger.debug("Getting market data for property ID: {}", propertyId);

        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isEmpty()) {
            throw new IllegalArgumentException("Property not found with ID: " + propertyId);
        }

        Property property = propertyOpt.get();
        
        // Vérifier que la propriété est en France
        String country = property.getCountry();
        if (country == null || !country.equalsIgnoreCase("France") && !country.equalsIgnoreCase("FR")) {
            throw new IllegalArgumentException("Les données DVF ne sont disponibles que pour les propriétés en France");
        }
        
        String codePostal = property.getPostalCode();
        String propertyType = property.getType();

        if (codePostal == null || codePostal.isEmpty()) {
            throw new IllegalArgumentException("Property must have a postal code for market analysis");
        }

        // Obtenir les données de marché de base
        MarketDataDTO marketData = getMarketData(codePostal, propertyType, startDate, endDate);

        // Calculer la comparaison avec la propriété
        if (property.getPrice() != null && property.getSurface() != null 
            && property.getSurface().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal propertyPricePerSquareMeter = property.getPrice()
                .divide(property.getSurface(), 2, RoundingMode.HALF_UP);

            MarketDataDTO.PropertyComparisonDTO comparison = new MarketDataDTO.PropertyComparisonDTO();
            comparison.setPropertyPricePerSquareMeter(propertyPricePerSquareMeter);

            if (marketData.getAveragePricePerSquareMeter() != null) {
                BigDecimal difference = propertyPricePerSquareMeter
                    .subtract(marketData.getAveragePricePerSquareMeter())
                    .divide(marketData.getAveragePricePerSquareMeter(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

                comparison.setPriceDifferencePercent(difference);

                // Évaluation
                if (difference.compareTo(BigDecimal.valueOf(10)) > 0) {
                    comparison.setPriceEvaluation("SURESTIMÉ");
                    comparison.setRecommendation("Le prix est supérieur de plus de 10% au marché. Considérez une réduction.");
                } else if (difference.compareTo(BigDecimal.valueOf(-10)) < 0) {
                    comparison.setPriceEvaluation("SOUS-ESTIMÉ");
                    comparison.setRecommendation("Le prix est inférieur de plus de 10% au marché. Vous pourriez augmenter le prix.");
                } else {
                    comparison.setPriceEvaluation("CORRECT");
                    comparison.setRecommendation("Le prix est aligné avec le marché local.");
                }
            }

            marketData.setComparison(comparison);
        }

        return marketData;
    }

    /**
     * Trouve des transactions similaires à une propriété
     */
    public List<DVFTransaction> findSimilarTransactions(Long propertyId, int limit) {
        logger.debug("Finding similar transactions for property ID: {}", propertyId);

        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isEmpty()) {
            throw new IllegalArgumentException("Property not found with ID: " + propertyId);
        }

        Property property = propertyOpt.get();
        String codePostal = property.getPostalCode();
        String propertyType = property.getType();
        BigDecimal surface = property.getSurface();

        if (codePostal == null || surface == null) {
            return List.of();
        }

        String dvfType = mapPropertyTypeToDVF(propertyType);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(2); // 2 dernières années

        // Recherche avec une tolérance de ±20% sur la surface
        BigDecimal minSurface = surface.multiply(BigDecimal.valueOf(0.8));
        BigDecimal maxSurface = surface.multiply(BigDecimal.valueOf(1.2));

        return dvfTransactionRepository.findSimilarTransactions(
            codePostal, dvfType, startDate, endDate, surface, minSurface, maxSurface,
            PageRequest.of(0, limit));
    }

    /**
     * Calcule l'évolution des prix par trimestre
     */
    private List<MarketDataDTO.PriceEvolutionDTO> calculatePriceEvolution(
            String codePostal, String dvfType, LocalDate startDate, LocalDate endDate) {
        
        List<MarketDataDTO.PriceEvolutionDTO> evolution = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDate quarterStart = current;
            LocalDate quarterEnd = current.plusMonths(3).minusDays(1);
            
            if (quarterEnd.isAfter(endDate)) {
                quarterEnd = endDate;
            }

            BigDecimal avgPrice = dvfTransactionRepository.calculateAveragePricePerSquareMeter(
                codePostal, dvfType, quarterStart, quarterEnd);
            Long count = dvfTransactionRepository.countByCodePostalAndTypeLocalAndMutationDateBetween(
                codePostal, dvfType, quarterStart, quarterEnd);

            if (avgPrice != null && count > 0) {
                MarketDataDTO.PriceEvolutionDTO period = new MarketDataDTO.PriceEvolutionDTO();
                period.setPeriod(String.format("%s-Q%d", quarterStart.getYear(), 
                    (quarterStart.getMonthValue() - 1) / 3 + 1));
                period.setAveragePrice(avgPrice);
                period.setCount(count);
                evolution.add(period);
            }

            current = quarterEnd.plusDays(1);
        }

        return evolution;
    }

    /**
     * Mappe le type de propriété de l'application vers le type DVF
     */
    private String mapPropertyTypeToDVF(String propertyType) {
        if (propertyType == null) {
            return null;
        }

        String upperType = propertyType.toUpperCase();
        if ("HOUSE".equals(upperType) || "MAISON".equals(upperType)) {
            return "Maison";
        } else if ("APARTMENT".equals(upperType) || "APPARTEMENT".equals(upperType)) {
            return "Appartement";
        } else if ("COMMERCIAL".equals(upperType)) {
            return "Local industriel. commercial ou assimilé";
        }
        return propertyType;
    }
}

