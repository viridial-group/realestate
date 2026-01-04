package com.realestate.property.service;

import com.realestate.common.client.dto.OrganizationInfoDTO;
import com.realestate.common.client.IdentityServiceClient;
import com.realestate.property.dto.OrganizationPerformanceStatsDTO;
import com.realestate.property.dto.OrganizationReviewCreateDTO;
import com.realestate.property.dto.OrganizationReviewDTO;
import com.realestate.property.dto.OrganizationReviewStatsDTO;
import com.realestate.property.dto.OrganizationWithReviewsDTO;
import com.realestate.property.entity.Property;
import com.realestate.property.entity.OrganizationReview;
import com.realestate.property.mapper.OrganizationReviewMapper;
import com.realestate.property.repository.OrganizationReviewRepository;
import com.realestate.property.repository.PropertyRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrganizationReviewService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationReviewService.class);

    private final OrganizationReviewRepository reviewRepository;
    private final OrganizationReviewMapper reviewMapper;
    private final PropertyRepository propertyRepository;
    @Autowired(required = false)
    private IdentityServiceClient identityServiceClient;

    public OrganizationReviewService(
            OrganizationReviewRepository reviewRepository,
            OrganizationReviewMapper reviewMapper,
            PropertyRepository propertyRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Récupérer toutes les organisations actives depuis identity-service
     */
    private List<OrganizationInfoDTO> fetchAllOrganizations() {
        try {
            if (identityServiceClient == null) {
                logger.warn("IdentityServiceClient is not available. Returning empty list.");
                return new ArrayList<>();
            }
            
            List<OrganizationInfoDTO> organizations = identityServiceClient.getAllOrganizations()
                    .block();
            
            return organizations != null ? organizations : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error fetching organizations from identity-service: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Créer un nouvel avis pour une organisation
     */
    @Transactional
    public OrganizationReviewDTO createReview(OrganizationReviewCreateDTO createDTO) {
        logger.debug("Creating review for organization ID: {}", createDTO.getOrganizationId());

        OrganizationReview review = reviewMapper.toEntity(createDTO);
        review.setStatus("PENDING"); // Par défaut, les avis sont en attente de modération
        review.setActive(true);
        review.setHelpfulCount(0);

        OrganizationReview saved = reviewRepository.save(review);
        return reviewMapper.toDTO(saved);
    }

    /**
     * Obtenir les avis approuvés d'une organisation
     */
    public Page<OrganizationReviewDTO> getApprovedReviews(Long organizationId, Pageable pageable) {
        Page<OrganizationReview> reviews = reviewRepository.findByOrganizationIdAndApproved(organizationId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Obtenir tous les avis d'une organisation (pour admin)
     */
    public Page<OrganizationReviewDTO> getAllReviews(Long organizationId, Pageable pageable) {
        Page<OrganizationReview> reviews = reviewRepository.findByOrganizationId(organizationId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Obtenir les statistiques des avis d'une organisation
     */
    public OrganizationReviewStatsDTO getReviewStats(Long organizationId) {
        Double averageRating = reviewRepository.getAverageRatingByOrganizationId(organizationId);
        Long totalReviews = reviewRepository.countApprovedReviewsByOrganizationId(organizationId);
        Long verifiedCount = reviewRepository.countVerifiedReviewsByOrganizationId(organizationId);

        OrganizationReviewStatsDTO stats = new OrganizationReviewStatsDTO();
        stats.setAverageRating(averageRating != null ? averageRating : 0.0);
        stats.setTotalReviews(totalReviews != null ? totalReviews : 0L);

        // Distribution des notes
        OrganizationReviewStatsDTO.RatingDistributionDTO distribution = 
            new OrganizationReviewStatsDTO.RatingDistributionDTO();
        distribution.setFiveStars(reviewRepository.countByOrganizationIdAndRating(organizationId, 5));
        distribution.setFourStars(reviewRepository.countByOrganizationIdAndRating(organizationId, 4));
        distribution.setThreeStars(reviewRepository.countByOrganizationIdAndRating(organizationId, 3));
        distribution.setTwoStars(reviewRepository.countByOrganizationIdAndRating(organizationId, 2));
        distribution.setOneStar(reviewRepository.countByOrganizationIdAndRating(organizationId, 1));
        stats.setRatingDistribution(distribution);

        // Pourcentage de clients vérifiés
        if (totalReviews != null && totalReviews > 0 && verifiedCount != null) {
            stats.setVerifiedClientPercentage((verifiedCount.doubleValue() / totalReviews.doubleValue()) * 100.0);
        } else {
            stats.setVerifiedClientPercentage(0.0);
        }

        return stats;
    }

    /**
     * Marquer un avis comme utile
     */
    @Transactional
    public void markAsHelpful(Long reviewId) {
        OrganizationReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    /**
     * Approuver un avis (pour admin)
     */
    @Transactional
    public OrganizationReviewDTO approveReview(Long reviewId) {
        OrganizationReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        review.setStatus("APPROVED");
        return reviewMapper.toDTO(reviewRepository.save(review));
    }

    /**
     * Rejeter un avis (pour admin)
     */
    @Transactional
    public OrganizationReviewDTO rejectReview(Long reviewId) {
        OrganizationReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        review.setStatus("REJECTED");
        return reviewMapper.toDTO(reviewRepository.save(review));
    }

    /**
     * Obtenir une organisation spécifique avec ses statistiques d'avis
     */
    public OrganizationWithReviewsDTO getOrganizationWithReviewsById(Long organizationId) {
        try {
            if (identityServiceClient == null) {
                logger.warn("IdentityServiceClient is not available. Cannot fetch organization.");
                return null;
            }
            
            // Récupérer l'organisation depuis identity-service
            Optional<OrganizationInfoDTO> orgOptional = identityServiceClient.getOrganizationByIdPublic(organizationId)
                    .block();
            
            if (orgOptional.isEmpty()) {
                return null;
            }
            
            OrganizationInfoDTO org = orgOptional.get();
            
            if (org.getActive() != null && !org.getActive()) {
                return null;
            }
            
            // Enrichir avec les statistiques d'avis
            OrganizationWithReviewsDTO dto = new OrganizationWithReviewsDTO();
            dto.setId(org.getId());
            dto.setName(org.getName());
            dto.setDescription(org.getDescription());
            dto.setLogoUrl(org.getLogoUrl());
            dto.setAddress(org.getAddress());
            dto.setCity(org.getCity());
            dto.setPostalCode(org.getPostalCode());
            dto.setCountry(org.getCountry());
            dto.setPhone(org.getPhone());
            dto.setEmail(org.getEmail());
            dto.setDomain(org.getDomain());
            dto.setDefaultOfficeHours(org.getDefaultOfficeHours());
            
            // Ajouter les statistiques d'avis
            OrganizationReviewStatsDTO stats = getReviewStats(organizationId);
            dto.setReviewStats(stats);
            
            // Ajouter le nombre de propriétés
            Long propertyCount = propertyRepository.countByOrganizationId(organizationId);
            dto.setPropertyCount(propertyCount != null ? propertyCount : 0L);
            
            return dto;
        } catch (Exception e) {
            logger.error("Error fetching organization {} from identity-service: {}", organizationId, e.getMessage());
            return null;
        }
    }

    /**
     * Obtenir les organisations avec leurs statistiques d'avis
     */
    public List<OrganizationWithReviewsDTO> getOrganizationsWithReviews(String city, String postalCode, Double minRating) {
        // Récupérer toutes les organisations actives depuis identity-service via WebClient
        List<OrganizationInfoDTO> organizations = fetchAllOrganizations();
        
        return organizations.stream()
            .filter(org -> org.getActive() != null && org.getActive())
            .filter(org -> {
                // Filtrer par ville si fourni
                if (city != null && !city.isEmpty()) {
                    return org.getCity() != null && org.getCity().equalsIgnoreCase(city);
                }
                return true;
            })
            .filter(org -> {
                // Filtrer par code postal si fourni
                if (postalCode != null && !postalCode.isEmpty()) {
                    return org.getPostalCode() != null && org.getPostalCode().equals(postalCode);
                }
                return true;
            })
            .map(org -> {
                OrganizationWithReviewsDTO dto = new OrganizationWithReviewsDTO();
                dto.setId(org.getId());
                dto.setName(org.getName());
                dto.setDescription(org.getDescription());
                dto.setLogoUrl(org.getLogoUrl());
                dto.setAddress(org.getAddress());
                dto.setCity(org.getCity());
                dto.setPostalCode(org.getPostalCode());
                dto.setCountry(org.getCountry());
                dto.setPhone(org.getPhone());
                dto.setEmail(org.getEmail());
                dto.setDomain(org.getDomain());
                dto.setDefaultOfficeHours(org.getDefaultOfficeHours());

                // Ajouter les statistiques d'avis
                OrganizationReviewStatsDTO stats = getReviewStats(org.getId());
                dto.setReviewStats(stats);

                // Compter les propriétés
                try {
                    Long propertyCount = propertyRepository.countByOrganizationId(org.getId());
                    dto.setPropertyCount(propertyCount != null ? propertyCount : 0L);
                } catch (Exception e) {
                    logger.warn("Error counting properties for organization {}: {}", org.getId(), e.getMessage());
                    dto.setPropertyCount(0L);
                }

                return dto;
            })
            .filter(dto -> {
                // Filtrer par note minimale si fourni
                if (minRating != null && dto.getReviewStats() != null) {
                    Double avgRating = dto.getReviewStats().getAverageRating();
                    return avgRating != null && avgRating >= minRating;
                }
                return true;
            })
            .sorted((a, b) -> {
                // Trier par note moyenne décroissante, puis par nombre d'avis
                Double ratingA = a.getReviewStats() != null ? a.getReviewStats().getAverageRating() : 0.0;
                Double ratingB = b.getReviewStats() != null ? b.getReviewStats().getAverageRating() : 0.0;
                int ratingCompare = Double.compare(ratingB, ratingA);
                if (ratingCompare != 0) {
                    return ratingCompare;
                }
                Long reviewsA = a.getReviewStats() != null ? a.getReviewStats().getTotalReviews() : 0L;
                Long reviewsB = b.getReviewStats() != null ? b.getReviewStats().getTotalReviews() : 0L;
                return Long.compare(reviewsB, reviewsA);
            })
            .collect(Collectors.toList());
    }

    /**
     * Obtenir les statistiques de performance d'une organisation
     */
    public OrganizationPerformanceStatsDTO getPerformanceStats(Long organizationId) {
        OrganizationPerformanceStatsDTO stats = new OrganizationPerformanceStatsDTO();
        
        List<Property> properties = propertyRepository.findActiveByOrganizationId(organizationId);
        
        if (properties.isEmpty()) {
            stats.setTotalProperties(0L);
            stats.setAvailableProperties(0L);
            stats.setSoldProperties(0L);
            stats.setRentedProperties(0L);
            stats.setAveragePrice(BigDecimal.ZERO);
            stats.setMinPrice(BigDecimal.ZERO);
            stats.setMaxPrice(BigDecimal.ZERO);
            stats.setAverageSurface(BigDecimal.ZERO);
            stats.setNewThisMonth(0L);
            stats.setNewThisWeek(0L);
            stats.setByType(new HashMap<>());
            stats.setByCity(new HashMap<>());
            stats.setByStatus(new HashMap<>());
            stats.setServedCities(new ArrayList<>());
            stats.setPropertyTypes(new ArrayList<>());
            return stats;
        }
        
        // Statistiques de base
        stats.setTotalProperties((long) properties.size());
        stats.setAvailableProperties(properties.stream()
            .filter(p -> "AVAILABLE".equals(p.getStatus()) || "PUBLISHED".equals(p.getStatus()))
            .count());
        stats.setSoldProperties(properties.stream()
            .filter(p -> "SOLD".equals(p.getStatus()))
            .count());
        stats.setRentedProperties(properties.stream()
            .filter(p -> "RENTED".equals(p.getStatus()))
            .count());
        
        // Statistiques de prix
        List<BigDecimal> prices = properties.stream()
            .filter(p -> p.getPrice() != null && p.getPrice().compareTo(BigDecimal.ZERO) > 0)
            .map(Property::getPrice)
            .collect(Collectors.toList());
        
        if (!prices.isEmpty()) {
            BigDecimal sum = prices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAveragePrice(sum.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP));
            stats.setMinPrice(prices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            stats.setMaxPrice(prices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        } else {
            stats.setAveragePrice(BigDecimal.ZERO);
            stats.setMinPrice(BigDecimal.ZERO);
            stats.setMaxPrice(BigDecimal.ZERO);
        }
        
        // Statistiques de surface
        List<BigDecimal> surfaces = properties.stream()
            .filter(p -> p.getSurface() != null && p.getSurface().compareTo(BigDecimal.ZERO) > 0)
            .map(Property::getSurface)
            .collect(Collectors.toList());
        
        if (!surfaces.isEmpty()) {
            BigDecimal sum = surfaces.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAverageSurface(sum.divide(BigDecimal.valueOf(surfaces.size()), 2, RoundingMode.HALF_UP));
        } else {
            stats.setAverageSurface(BigDecimal.ZERO);
        }
        
        // Propriétés récentes
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minus(1, ChronoUnit.MONTHS);
        LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
        
        stats.setNewThisMonth(properties.stream()
            .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(monthAgo))
            .count());
        stats.setNewThisWeek(properties.stream()
            .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(weekAgo))
            .count());
        
        // Répartition par type
        Map<String, Long> byType = properties.stream()
            .filter(p -> p.getType() != null)
            .collect(Collectors.groupingBy(
                Property::getType,
                Collectors.counting()
            ));
        stats.setByType(byType);
        
        // Répartition par ville
        Map<String, Long> byCity = properties.stream()
            .filter(p -> p.getCity() != null && !p.getCity().isEmpty())
            .collect(Collectors.groupingBy(
                Property::getCity,
                Collectors.counting()
            ));
        stats.setByCity(byCity);
        
        // Répartition par statut
        Map<String, Long> byStatus = properties.stream()
            .filter(p -> p.getStatus() != null)
            .collect(Collectors.groupingBy(
                Property::getStatus,
                Collectors.counting()
            ));
        stats.setByStatus(byStatus);
        
        // Villes desservies (distinctes)
        List<String> servedCities = properties.stream()
            .filter(p -> p.getCity() != null && !p.getCity().isEmpty())
            .map(Property::getCity)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        stats.setServedCities(servedCities);
        
        // Types de propriétés gérés (distincts)
        List<String> propertyTypes = properties.stream()
            .filter(p -> p.getType() != null && !p.getType().isEmpty())
            .map(Property::getType)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        stats.setPropertyTypes(propertyTypes);
        
        return stats;
    }
}

