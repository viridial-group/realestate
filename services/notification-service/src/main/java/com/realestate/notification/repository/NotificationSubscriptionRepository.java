package com.realestate.notification.repository;

import com.realestate.notification.entity.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {

    List<NotificationSubscription> findByUserId(Long userId);

    List<NotificationSubscription> findByOrganizationId(Long organizationId);

    @Query("SELECT s FROM NotificationSubscription s WHERE s.userId = :userId AND s.active = true AND s.enabled = true")
    List<NotificationSubscription> findActiveEnabledByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM NotificationSubscription s WHERE s.userId = :userId AND s.notificationType = :notificationType AND s.active = true")
    Optional<NotificationSubscription> findActiveByUserIdAndType(
            @Param("userId") Long userId,
            @Param("notificationType") String notificationType
    );

    @Query("SELECT s FROM NotificationSubscription s WHERE s.organizationId = :organizationId AND s.notificationType = :notificationType AND s.active = true AND s.enabled = true")
    List<NotificationSubscription> findActiveEnabledByOrganizationIdAndType(
            @Param("organizationId") Long organizationId,
            @Param("notificationType") String notificationType
    );
}

