package com.realestate.billing.specification;

import com.realestate.billing.entity.Invoice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Specifications JPA pour filtrer les factures selon les permissions
 */
public class InvoiceSpecification {

    /**
     * Filtre par organisation
     */
    public static Specification<Invoice> hasOrganization(Long organizationId) {
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
    public static Specification<Invoice> hasAnyOrganization(Set<Long> organizationIds) {
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
    public static Specification<Invoice> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filtre par abonnement
     */
    public static Specification<Invoice> hasSubscription(Long subscriptionId) {
        return (root, query, cb) -> {
            if (subscriptionId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.join("subscription").get("id"), subscriptionId);
        };
    }

    /**
     * Filtre par factures en retard
     */
    public static Specification<Invoice> overdueBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.and(
                    cb.equal(root.get("status"), "PENDING"),
                    cb.isNotNull(root.get("dueDate")),
                    cb.lessThan(root.get("dueDate"), date)
            );
        };
    }
}

