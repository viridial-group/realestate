package com.realestate.audit.specification;

import com.realestate.audit.entity.AuditLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Specifications JPA pour filtrer les logs d'audit selon les permissions
 */
public class AuditLogSpecification {

    /**
     * Filtre par organisation
     */
    public static Specification<AuditLog> hasOrganization(Long organizationId) {
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
    public static Specification<AuditLog> hasAnyOrganization(Set<Long> organizationIds) {
        return (root, query, cb) -> {
            if (organizationIds == null || organizationIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("organizationId").in(organizationIds);
        };
    }

    /**
     * Filtre par acteur
     */
    public static Specification<AuditLog> hasActor(Long actorId) {
        return (root, query, cb) -> {
            if (actorId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("actorId"), actorId);
        };
    }

    /**
     * Filtre par action
     */
    public static Specification<AuditLog> hasAction(String action) {
        return (root, query, cb) -> {
            if (action == null || action.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("action"), action);
        };
    }

    /**
     * Filtre par statut
     */
    public static Specification<AuditLog> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filtre par type de cible
     */
    public static Specification<AuditLog> hasTargetType(String targetType) {
        return (root, query, cb) -> {
            if (targetType == null || targetType.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("targetType"), targetType);
        };
    }

    /**
     * Filtre par ID de cible
     */
    public static Specification<AuditLog> hasTargetId(Long targetId) {
        return (root, query, cb) -> {
            if (targetId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("targetId"), targetId);
        };
    }

    /**
     * Filtre par plage de dates
     */
    public static Specification<AuditLog> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

