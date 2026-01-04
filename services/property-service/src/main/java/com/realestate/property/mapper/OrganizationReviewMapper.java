package com.realestate.property.mapper;

import com.realestate.property.dto.OrganizationReviewCreateDTO;
import com.realestate.property.dto.OrganizationReviewDTO;
import com.realestate.property.entity.OrganizationReview;
import org.springframework.stereotype.Component;

@Component
public class OrganizationReviewMapper {

    public OrganizationReviewDTO toDTO(OrganizationReview review) {
        if (review == null) {
            return null;
        }

        OrganizationReviewDTO dto = new OrganizationReviewDTO();
        dto.setId(review.getId());
        dto.setOrganizationId(review.getOrganizationId());
        dto.setUserId(review.getUserId());
        dto.setAuthorName(review.getAuthorName());
        dto.setAuthorEmail(review.getAuthorEmail());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setStatus(review.getStatus());
        dto.setVerifiedClient(review.getVerifiedClient());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setTransactionType(review.getTransactionType());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());

        return dto;
    }

    public OrganizationReview toEntity(OrganizationReviewCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        OrganizationReview review = new OrganizationReview();
        review.setOrganizationId(dto.getOrganizationId());
        review.setUserId(dto.getUserId());
        review.setAuthorName(dto.getAuthorName());
        review.setAuthorEmail(dto.getAuthorEmail());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setStatus("PENDING"); // Par défaut, les avis sont en attente de modération
        review.setVerifiedClient(dto.getVerifiedClient() != null ? dto.getVerifiedClient() : false);
        review.setHelpfulCount(0);
        review.setTransactionType(dto.getTransactionType());
        review.setActive(true);

        return review;
    }
}

