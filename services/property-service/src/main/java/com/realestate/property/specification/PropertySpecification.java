package com.realestate.property.specification;

import com.realestate.property.entity.Property;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifications JPA pour filtrer les propriétés
 */
public class PropertySpecification {

    /**
     * Filtre par organisation
     */
    public static Specification<Property> hasOrganization(Long organizationId) {
        return (root, query, cb) -> {
            if (organizationId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("organizationId"), organizationId);
        };
    }

    /**
     * Filtre par utilisateur assigné
     */
    public static Specification<Property> hasAssignedUser(Long assignedUserId) {
        return (root, query, cb) -> {
            if (assignedUserId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("assignedUserId"), assignedUserId);
        };
    }

    /**
     * Filtre par team
     */
    public static Specification<Property> hasTeam(Long teamId) {
        return (root, query, cb) -> {
            if (teamId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("teamId"), teamId);
        };
    }

    /**
     * Filtre par statut
     */
    public static Specification<Property> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filtre par type
     */
    public static Specification<Property> hasType(String type) {
        return (root, query, cb) -> {
            if (type == null || type.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("type"), type);
        };
    }

    /**
     * Filtre par ville
     */
    public static Specification<Property> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("city"), city);
        };
    }

    /**
     * Filtre par pays
     */
    public static Specification<Property> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("country"), country);
        };
    }

    /**
     * Filtre par plage de prix
     */
    public static Specification<Property> hasPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            return predicates.isEmpty() 
                ? cb.conjunction() 
                : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filtre par plage de surface
     */
    public static Specification<Property> hasSurfaceRange(BigDecimal minSurface, BigDecimal maxSurface) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minSurface != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("surface"), minSurface));
            }
            if (maxSurface != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("surface"), maxSurface));
            }
            
            return predicates.isEmpty() 
                ? cb.conjunction() 
                : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filtre par nombre de chambres
     */
    public static Specification<Property> hasBedrooms(Integer bedrooms) {
        return (root, query, cb) -> {
            if (bedrooms == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("bedrooms"), bedrooms);
        };
    }

    /**
     * Filtre par nombre de salles de bain
     */
    public static Specification<Property> hasBathrooms(Integer bathrooms) {
        return (root, query, cb) -> {
            if (bathrooms == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("bathrooms"), bathrooms);
        };
    }

    /**
     * Filtre par propriétés actives uniquement
     */
    public static Specification<Property> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                // Par défaut, ne filtrer que les actives
                return cb.equal(root.get("active"), true);
            }
            return cb.equal(root.get("active"), active);
        };
    }

    /**
     * Recherche textuelle dans titre, description, adresse, ville
     */
    public static Specification<Property> searchByText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern),
                cb.like(cb.lower(root.get("address")), pattern),
                cb.like(cb.lower(root.get("city")), pattern),
                cb.like(cb.lower(root.get("reference")), pattern)
            );
        };
    }

    /**
     * Combine plusieurs specifications avec AND
     */
    public static Specification<Property> combine(Specification<Property>... specifications) {
        Specification<Property> combined = null;
        for (Specification<Property> spec : specifications) {
            if (spec != null) {
                combined = combined == null ? spec : combined.and(spec);
            }
        }
        return combined != null ? combined : (root, query, cb) -> cb.conjunction();
    }
}

