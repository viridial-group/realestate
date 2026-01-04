package com.realestate.property.mapper;

import com.realestate.property.dto.ReviewCreateDTO;
import com.realestate.property.dto.ReviewDTO;
import com.realestate.property.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setPropertyId(review.getPropertyId());
        dto.setUserId(review.getUserId());
        dto.setAuthorName(review.getAuthorName());
        dto.setAuthorEmail(review.getAuthorEmail());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setStatus(review.getStatus());
        dto.setVerifiedPurchase(review.getVerifiedPurchase());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());

        return dto;
    }

    public Review toEntity(ReviewCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Review review = new Review();
        review.setPropertyId(dto.getPropertyId());
        review.setUserId(dto.getUserId());
        review.setAuthorName(dto.getAuthorName());
        review.setAuthorEmail(dto.getAuthorEmail());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setStatus("PENDING"); // Par défaut, les avis sont en attente de modération
        review.setVerifiedPurchase(dto.getVerifiedPurchase() != null ? dto.getVerifiedPurchase() : false);
        review.setHelpfulCount(0);
        review.setActive(true);

        return review;
    }

    public Review toEntity(ReviewDTO dto) {
        if (dto == null) {
            return null;
        }

        Review review = new Review();
        review.setId(dto.getId());
        review.setPropertyId(dto.getPropertyId());
        review.setUserId(dto.getUserId());
        review.setAuthorName(dto.getAuthorName());
        review.setAuthorEmail(dto.getAuthorEmail());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        review.setVerifiedPurchase(dto.getVerifiedPurchase() != null ? dto.getVerifiedPurchase() : false);
        review.setHelpfulCount(dto.getHelpfulCount() != null ? dto.getHelpfulCount() : 0);
        review.setActive(true);

        return review;
    }
}

