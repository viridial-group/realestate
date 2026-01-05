package com.realestate.billing.repository;

import com.realestate.billing.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription> {

    Optional<Subscription> findByOrganizationId(Long organizationId);

    List<Subscription> findByStatus(String status);

    List<Subscription> findByActiveTrue();

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate IS NOT NULL AND s.endDate <= :date")
    List<Subscription> findExpiringSubscriptions(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.trialEndDate IS NOT NULL AND s.trialEndDate <= :date")
    List<Subscription> findExpiringTrials(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.organizationId = :organizationId AND s.active = true")
    Optional<Subscription> findActiveByOrganizationId(@Param("organizationId") Long organizationId);
}

