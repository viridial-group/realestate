package com.realestate.property.repository;

import com.realestate.property.entity.OrganizationReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationReviewRepository extends JpaRepository<OrganizationReview, Long> {

    @Query("SELECT r FROM OrganizationReview r WHERE r.organizationId = :organizationId AND r.active = true AND r.status = 'APPROVED' ORDER BY r.createdAt DESC")
    Page<OrganizationReview> findByOrganizationIdAndApproved(@Param("organizationId") Long organizationId, Pageable pageable);

    @Query("SELECT r FROM OrganizationReview r WHERE r.organizationId = :organizationId AND r.active = true ORDER BY r.createdAt DESC")
    Page<OrganizationReview> findByOrganizationId(@Param("organizationId") Long organizationId, Pageable pageable);

    @Query("SELECT r FROM OrganizationReview r WHERE r.userId = :userId AND r.active = true ORDER BY r.createdAt DESC")
    Page<OrganizationReview> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM OrganizationReview r WHERE r.status = :status AND r.active = true ORDER BY r.createdAt DESC")
    Page<OrganizationReview> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM OrganizationReview r WHERE r.organizationId = :organizationId AND r.active = true AND r.status = 'APPROVED'")
    Double getAverageRatingByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT COUNT(r) FROM OrganizationReview r WHERE r.organizationId = :organizationId AND r.active = true AND r.status = 'APPROVED'")
    Long countApprovedReviewsByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT COUNT(r) FROM OrganizationReview r WHERE r.organizationId = :organizationId AND r.rating = :rating AND r.active = true AND r.status = 'APPROVED'")
    Long countByOrganizationIdAndRating(@Param("organizationId") Long organizationId, @Param("rating") Integer rating);

    @Query("SELECT COUNT(r) FROM OrganizationReview r WHERE r.organizationId = :organizationId AND r.verifiedClient = true AND r.active = true AND r.status = 'APPROVED'")
    Long countVerifiedReviewsByOrganizationId(@Param("organizationId") Long organizationId);
}

