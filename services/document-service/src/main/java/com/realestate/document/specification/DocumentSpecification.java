package com.realestate.document.specification;

import com.realestate.document.entity.Document;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Specifications JPA pour filtrer les documents selon les permissions
 */
public class DocumentSpecification {

    /**
     * Filtre par organisation
     */
    public static Specification<Document> hasOrganization(Long organizationId) {
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
    public static Specification<Document> hasAnyOrganization(Set<Long> organizationIds) {
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
    public static Specification<Document> hasCreatedBy(Long createdBy) {
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
    public static Specification<Document> accessibleByUser(Long userId, Set<Long> accessibleOrganizationIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // L'utilisateur peut voir ses propres documents
            if (userId != null) {
                predicates.add(cb.equal(root.get("createdBy"), userId));
            }
            
            // L'utilisateur peut voir les documents de ses organisations accessibles
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
     * Filtre par propriété
     */
    public static Specification<Document> hasProperty(Long propertyId) {
        return (root, query, cb) -> {
            if (propertyId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("propertyId"), propertyId);
        };
    }

    /**
     * Filtre par ressource
     */
    public static Specification<Document> hasResource(Long resourceId) {
        return (root, query, cb) -> {
            if (resourceId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("resourceId"), resourceId);
        };
    }

    /**
     * Filtre par documents actifs uniquement
     */
    public static Specification<Document> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.equal(root.get("active"), true);
            }
            return cb.equal(root.get("active"), active);
        };
    }
}

