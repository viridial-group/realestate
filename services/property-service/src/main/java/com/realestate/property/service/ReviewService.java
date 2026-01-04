package com.realestate.property.service;

import com.realestate.property.dto.ReviewCreateDTO;
import com.realestate.property.dto.ReviewDTO;
import com.realestate.property.dto.ReviewStatsDTO;
import com.realestate.property.entity.Review;
import com.realestate.property.mapper.ReviewMapper;
import com.realestate.property.repository.PropertyRepository;
import com.realestate.property.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final PropertyRepository propertyRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper, PropertyRepository propertyRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Créer un nouvel avis
     */
    public ReviewDTO createReview(ReviewCreateDTO reviewCreateDTO) {
        logger.debug("Creating review for property ID: {}", reviewCreateDTO.getPropertyId());

        // Vérifier que la propriété existe
        if (!propertyRepository.existsById(reviewCreateDTO.getPropertyId())) {
            throw new IllegalArgumentException("La propriété avec l'ID " + reviewCreateDTO.getPropertyId() + " n'existe pas");
        }

        // Vérifier si l'utilisateur a déjà laissé un avis pour cette propriété
        if (reviewCreateDTO.getUserId() != null) {
            Review existingReview = reviewRepository.findByPropertyIdAndUserId(
                    reviewCreateDTO.getPropertyId(),
                    reviewCreateDTO.getUserId()
            );
            if (existingReview != null && existingReview.getActive()) {
                throw new IllegalStateException("Vous avez déjà laissé un avis pour cette propriété");
            }
        }

        // Valider que authorName est fourni si userId n'est pas fourni
        if (reviewCreateDTO.getUserId() == null && 
            (reviewCreateDTO.getAuthorName() == null || reviewCreateDTO.getAuthorName().trim().isEmpty())) {
            throw new IllegalArgumentException("Le nom de l'auteur est requis si l'ID utilisateur n'est pas fourni");
        }

        Review review = reviewMapper.toEntity(reviewCreateDTO);
        Review savedReview = reviewRepository.save(review);
        logger.info("Review created with ID: {}", savedReview.getId());

        ReviewDTO dto = reviewMapper.toDTO(savedReview);
        
        // Enrichir avec le titre de la propriété
        propertyRepository.findById(dto.getPropertyId())
                .ifPresent(property -> dto.setPropertyTitle(property.getTitle()));
        
        return dto;
    }

    /**
     * Récupérer les avis approuvés d'une propriété
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getApprovedReviewsByPropertyId(Long propertyId, Pageable pageable) {
        logger.debug("Fetching approved reviews for property ID: {}", propertyId);
        Page<ReviewDTO> reviews = reviewRepository.findByPropertyIdAndApproved(propertyId, pageable)
                .map(reviewMapper::toDTO);
        
        // Enrichir avec le titre de la propriété
        propertyRepository.findById(propertyId)
                .ifPresent(property -> reviews.getContent().forEach(reviewDTO -> 
                    reviewDTO.setPropertyTitle(property.getTitle())));
        
        return reviews;
    }

    /**
     * Récupérer tous les avis d'une propriété (pour admin)
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByPropertyId(Long propertyId, Pageable pageable) {
        logger.debug("Fetching all reviews for property ID: {}", propertyId);
        Page<ReviewDTO> reviews = reviewRepository.findByPropertyId(propertyId, pageable)
                .map(reviewMapper::toDTO);
        
        // Enrichir avec le titre de la propriété
        propertyRepository.findById(propertyId)
                .ifPresent(property -> reviews.getContent().forEach(reviewDTO -> 
                    reviewDTO.setPropertyTitle(property.getTitle())));
        
        return reviews;
    }

    /**
     * Récupérer les avis d'un utilisateur
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByUserId(Long userId, Pageable pageable) {
        logger.debug("Fetching reviews for user ID: {}", userId);
        Page<ReviewDTO> reviews = reviewRepository.findByUserId(userId, pageable)
                .map(reviewMapper::toDTO);
        
        // Enrichir avec les titres des propriétés
        reviews.getContent().forEach(reviewDTO -> {
            if (reviewDTO.getPropertyId() != null) {
                propertyRepository.findById(reviewDTO.getPropertyId())
                        .ifPresent(property -> reviewDTO.setPropertyTitle(property.getTitle()));
            }
        });
        
        return reviews;
    }

    /**
     * Récupérer un avis par ID
     */
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long id) {
        logger.debug("Fetching review with ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avis non trouvé avec l'ID: " + id));
        ReviewDTO dto = reviewMapper.toDTO(review);
        
        // Enrichir avec le titre de la propriété
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId())
                    .ifPresent(property -> dto.setPropertyTitle(property.getTitle()));
        }
        
        return dto;
    }

    /**
     * Mettre à jour le statut d'un avis
     */
    public ReviewDTO updateReviewStatus(Long id, String status) {
        logger.debug("Updating review status for ID: {} to status: {}", id, status);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avis non trouvé avec l'ID: " + id));

        if (!List.of("PENDING", "APPROVED", "REJECTED").contains(status)) {
            throw new IllegalArgumentException("Statut invalide: " + status);
        }

        review.setStatus(status);
        Review updatedReview = reviewRepository.save(review);
        logger.info("Review status updated for ID: {} to status: {}", id, status);

        ReviewDTO dto = reviewMapper.toDTO(updatedReview);
        
        // Enrichir avec le titre de la propriété
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId())
                    .ifPresent(property -> dto.setPropertyTitle(property.getTitle()));
        }
        
        return dto;
    }

    /**
     * Marquer un avis comme utile
     */
    public ReviewDTO markHelpful(Long id) {
        logger.debug("Marking review as helpful for ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avis non trouvé avec l'ID: " + id));

        review.setHelpfulCount((review.getHelpfulCount() != null ? review.getHelpfulCount() : 0) + 1);
        Review updatedReview = reviewRepository.save(review);
        logger.info("Review marked as helpful for ID: {}", id);

        ReviewDTO dto = reviewMapper.toDTO(updatedReview);
        
        // Enrichir avec le titre de la propriété
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId())
                    .ifPresent(property -> dto.setPropertyTitle(property.getTitle()));
        }
        
        return dto;
    }

    /**
     * Supprimer un avis (soft delete)
     */
    public void deleteReview(Long id) {
        logger.debug("Deleting review with ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avis non trouvé avec l'ID: " + id));

        review.setActive(false);
        reviewRepository.save(review);
        logger.info("Review deleted (soft delete) with ID: {}", id);
    }

    /**
     * Récupérer les statistiques des avis pour une propriété
     */
    @Transactional(readOnly = true)
    public ReviewStatsDTO getReviewStatsByPropertyId(Long propertyId) {
        logger.debug("Fetching review stats for property ID: {}", propertyId);

        Double averageRating = reviewRepository.getAverageRatingByPropertyId(propertyId);
        Long totalReviews = reviewRepository.countApprovedReviewsByPropertyId(propertyId);
        List<Object[]> ratingDistribution = reviewRepository.getRatingDistributionByPropertyId(propertyId);

        ReviewStatsDTO stats = new ReviewStatsDTO(
                averageRating != null ? averageRating : 0.0,
                totalReviews != null ? totalReviews : 0L
        );

        // Construire la distribution des notes
        Map<Integer, Long> distribution = new HashMap<>();
        Map<Integer, Double> percentages = new HashMap<>();

        if (ratingDistribution != null && !ratingDistribution.isEmpty()) {
            for (Object[] row : ratingDistribution) {
                Integer rating = ((Number) row[0]).intValue();
                Long count = ((Number) row[1]).longValue();
                distribution.put(rating, count);
            }

            // Calculer les pourcentages
            if (totalReviews != null && totalReviews > 0) {
                for (Map.Entry<Integer, Long> entry : distribution.entrySet()) {
                    double percentage = (entry.getValue().doubleValue() / totalReviews.doubleValue()) * 100.0;
                    percentages.put(entry.getKey(), percentage);
                }
            }
        }

        stats.setRatingDistribution(distribution);
        stats.setRatingPercentages(percentages);

        return stats;
    }

    /**
     * Récupérer les avis par statut (pour admin)
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByStatus(String status, Pageable pageable) {
        logger.debug("Fetching reviews with status: {}", status);
        Page<ReviewDTO> reviews = reviewRepository.findByStatus(status, pageable)
                .map(reviewMapper::toDTO);
        
        // Enrichir avec les titres des propriétés
        reviews.getContent().forEach(reviewDTO -> {
            if (reviewDTO.getPropertyId() != null) {
                propertyRepository.findById(reviewDTO.getPropertyId())
                        .ifPresent(property -> reviewDTO.setPropertyTitle(property.getTitle()));
            }
        });
        
        return reviews;
    }
}

