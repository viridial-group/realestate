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
     * Filtre par prix minimum
     */
    public static Specification<Property> hasMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    /**
     * Filtre par prix maximum
     */
    public static Specification<Property> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    /**
     * Filtre par surface minimum
     */
    public static Specification<Property> hasMinSurface(BigDecimal minSurface) {
        return (root, query, cb) -> {
            if (minSurface == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("surface"), minSurface);
        };
    }

    /**
     * Filtre par surface maximum
     */
    public static Specification<Property> hasMaxSurface(BigDecimal maxSurface) {
        return (root, query, cb) -> {
            if (maxSurface == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("surface"), maxSurface);
        };
    }

    /**
     * Recherche dans le titre
     */
    public static Specification<Property> hasTitleContaining(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("title")), pattern);
        };
    }

    /**
     * Recherche dans la description
     */
    public static Specification<Property> hasDescriptionContaining(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("description")), pattern);
        };
    }

    /**
     * Recherche dans la ville
     */
    public static Specification<Property> hasCityContaining(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("city")), pattern);
        };
    }

    /**
     * Filtre par date de création (après une date donnée)
     */
    public static Specification<Property> createdAfter(java.time.LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    /**
     * Filtre par date de création (avant une date donnée)
     */
    public static Specification<Property> createdBefore(java.time.LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    /**
     * Recherche avancée style Google dans tous les champs textuels
     * Supporte les phrases exactes, termes multiples, et exclusion
     */
    public static Specification<Property> advancedTextSearch(
            List<String> includeTerms,
            List<String> exactPhrases,
            List<String> excludeTerms) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Phrases exactes (doivent être présentes dans au moins un champ)
            if (exactPhrases != null && !exactPhrases.isEmpty()) {
                for (String phrase : exactPhrases) {
                    Predicate phrasePredicate = cb.or(
                            cb.like(cb.lower(root.get("title")), "%" + phrase + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + phrase + "%"),
                            cb.like(cb.lower(root.get("city")), "%" + phrase + "%")
                    );
                    predicates.add(phrasePredicate);
                }
            }

            // Termes à inclure (logique améliorée : au moins un terme doit être présent)
            // Si un seul terme, il doit être présent dans au moins un champ
            // Si plusieurs termes, au moins un terme doit être présent (recherche plus permissive)
            if (includeTerms != null && !includeTerms.isEmpty()) {
                if (includeTerms.size() == 1) {
                    // Un seul terme : doit être présent dans au moins un champ
                    String term = includeTerms.get(0);
                    Predicate termPredicate = cb.or(
                            cb.like(cb.lower(root.get("title")), "%" + term + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + term + "%"),
                            cb.like(cb.lower(root.get("city")), "%" + term + "%"),
                            cb.like(cb.lower(root.get("address")), "%" + term + "%")
                    );
                    predicates.add(termPredicate);
                } else {
                    // Plusieurs termes : au moins un terme doit être présent (OR entre termes)
                    // Mais on privilégie les résultats avec plus de termes (recherche hybride)
                    List<Predicate> termPredicates = new ArrayList<>();
                    for (String term : includeTerms) {
                        Predicate termPredicate = cb.or(
                                cb.like(cb.lower(root.get("title")), "%" + term + "%"),
                                cb.like(cb.lower(root.get("description")), "%" + term + "%"),
                                cb.like(cb.lower(root.get("city")), "%" + term + "%"),
                                cb.like(cb.lower(root.get("address")), "%" + term + "%")
                        );
                        termPredicates.add(termPredicate);
                    }
                    // Au moins un terme doit correspondre (OR)
                    predicates.add(cb.or(termPredicates.toArray(new Predicate[0])));
                }
            }

            // Termes à exclure
            if (excludeTerms != null && !excludeTerms.isEmpty()) {
                for (String term : excludeTerms) {
                    Predicate excludePredicate = cb.and(
                            cb.not(cb.like(cb.lower(root.get("title")), "%" + term + "%")),
                            cb.not(cb.like(cb.lower(root.get("description")), "%" + term + "%")),
                            cb.not(cb.like(cb.lower(root.get("city")), "%" + term + "%"))
                    );
                    predicates.add(excludePredicate);
                }
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
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

