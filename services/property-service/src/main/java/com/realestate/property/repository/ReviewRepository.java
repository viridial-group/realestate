package com.realestate.property.repository;

import com.realestate.property.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.propertyId = :propertyId AND r.active = true AND r.status = 'APPROVED' ORDER BY r.createdAt DESC")
    Page<Review> findByPropertyIdAndApproved(@Param("propertyId") Long propertyId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.propertyId = :propertyId AND r.active = true ORDER BY r.createdAt DESC")
    Page<Review> findByPropertyId(@Param("propertyId") Long propertyId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.userId = :userId AND r.active = true ORDER BY r.createdAt DESC")
    Page<Review> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.status = :status AND r.active = true ORDER BY r.createdAt DESC")
    Page<Review> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.propertyId = :propertyId AND r.active = true AND r.status = 'APPROVED'")
    Double getAverageRatingByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.propertyId = :propertyId AND r.active = true AND r.status = 'APPROVED'")
    Long countApprovedReviewsByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.propertyId = :propertyId AND r.active = true AND r.status = 'APPROVED' GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT r FROM Review r WHERE r.propertyId = :propertyId AND r.userId = :userId AND r.active = true")
    Review findByPropertyIdAndUserId(@Param("propertyId") Long propertyId, @Param("userId") Long userId);
}

