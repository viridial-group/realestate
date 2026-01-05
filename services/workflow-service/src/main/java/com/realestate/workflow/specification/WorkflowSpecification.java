package com.realestate.workflow.specification;

import com.realestate.workflow.entity.ApprovalWorkflow;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Specifications JPA pour filtrer les workflows selon les permissions
 */
public class WorkflowSpecification {

    /**
     * Filtre par organisation
     */
    public static Specification<ApprovalWorkflow> hasOrganization(Long organizationId) {
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
    public static Specification<ApprovalWorkflow> hasAnyOrganization(Set<Long> organizationIds) {
        return (root, query, cb) -> {
            if (organizationIds == null || organizationIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("organizationId").in(organizationIds);
        };
    }

    /**
     * Filtre par créateur
     */
    public static Specification<ApprovalWorkflow> hasCreatedBy(Long createdBy) {
        return (root, query, cb) -> {
            if (createdBy == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("createdBy"), createdBy);
        };
    }

    /**
     * Filtre par créateur OU organisation accessible
     * Utilisé pour filtrer selon les permissions utilisateur
     */
    public static Specification<ApprovalWorkflow> accessibleByUser(Long userId, Set<Long> accessibleOrganizationIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // L'utilisateur peut voir ses propres workflows
            if (userId != null) {
                predicates.add(cb.equal(root.get("createdBy"), userId));
            }
            
            // L'utilisateur peut voir les workflows de ses organisations accessibles
            if (accessibleOrganizationIds != null && !accessibleOrganizationIds.isEmpty()) {
                predicates.add(root.get("organizationId").in(accessibleOrganizationIds));
            }
            
            if (predicates.isEmpty()) {
                return cb.disjunction(); // Aucun accès si aucune condition
            }
            
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filtre par action
     */
    public static Specification<ApprovalWorkflow> hasAction(String action) {
        return (root, query, cb) -> {
            if (action == null || action.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("action"), action);
        };
    }

    /**
     * Filtre par type de cible
     */
    public static Specification<ApprovalWorkflow> hasTargetType(String targetType) {
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
    public static Specification<ApprovalWorkflow> hasTargetId(Long targetId) {
        return (root, query, cb) -> {
            if (targetId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("targetId"), targetId);
        };
    }

    /**
     * Filtre par workflows actifs uniquement
     */
    public static Specification<ApprovalWorkflow> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.equal(root.get("active"), true);
            }
            return cb.equal(root.get("active"), active);
        };
    }

    /**
     * Filtre par workflow par défaut
     */
    public static Specification<ApprovalWorkflow> isDefault(Boolean isDefault) {
        return (root, query, cb) -> {
            if (isDefault == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("isDefault"), isDefault);
        };
    }
}

