package com.realestate.billing.specification;

import com.realestate.billing.entity.Subscription;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Specifications JPA pour filtrer les abonnements selon les permissions
 */
public class SubscriptionSpecification {

    /**
     * Filtre par organisation
     */
    public static Specification<Subscription> hasOrganization(Long organizationId) {
        return (root, query, cb) -> {
            if (organizationId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("organizationId"), organizationId);
        };
    }

    /**
     * Filtre par plusieurs organisations (pour inclure les sous-organisations)
     */
    public static Specification<Subscription> hasAnyOrganization(Set<Long> organizationIds) {
        return (root, query, cb) -> {
            if (organizationIds == null || organizationIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("organizationId").in(organizationIds);
        };
    }

    /**
     * Filtre par statut
     */
    public static Specification<Subscription> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filtre par abonnements actifs uniquement
     */
    public static Specification<Subscription> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.equal(root.get("active"), true);
            }
            return cb.equal(root.get("active"), active);
        };
    }

    /**
     * Filtre par abonnements expirant avant une date
     */
    public static Specification<Subscription> expiringBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.and(
                    cb.equal(root.get("status"), "ACTIVE"),
                    cb.isNotNull(root.get("endDate")),
                    cb.lessThanOrEqualTo(root.get("endDate"), date)
            );
        };
    }
}

