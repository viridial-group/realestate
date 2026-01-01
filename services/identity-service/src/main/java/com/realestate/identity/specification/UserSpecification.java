package com.realestate.identity.specification;

import com.realestate.identity.entity.OrganizationUser;
import com.realestate.identity.entity.Role;
import com.realestate.identity.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specifications JPA pour filtrer les utilisateurs
 */
public class UserSpecification {

    /**
     * Filtre par organisation via OrganizationUser
     * Note: On utilise une sous-requête car OrganizationUser n'a pas de relation JPA directe avec User
     */
    public static Specification<User> hasOrganization(Long organizationId) {
        return (root, query, cb) -> {
            if (organizationId == null) {
                return cb.conjunction();
            }
            // Sous-requête pour trouver les userId dans OrganizationUser
            jakarta.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
            jakarta.persistence.criteria.Root<OrganizationUser> orgUserRoot = subquery.from(OrganizationUser.class);
            subquery.select(orgUserRoot.get("userId"))
                    .where(cb.and(
                        cb.equal(orgUserRoot.get("organization").get("id"), organizationId),
                        cb.equal(orgUserRoot.get("active"), true)
                    ));
            return root.get("id").in(subquery);
        };
    }

    /**
     * Filtre par rôle
     */
    public static Specification<User> hasRole(String roleName) {
        return (root, query, cb) -> {
            if (roleName == null || roleName.isEmpty()) {
                return cb.conjunction();
            }
            // Utiliser distinct pour éviter les doublons avec JOIN
            if (query.getResultType() == Long.class || query.getResultType() == long.class) {
                // Pour les requêtes de count, ne pas ajouter distinct
            } else {
                query.distinct(true);
            }
            Join<User, Role> rolesJoin = root.join("roles", JoinType.INNER);
            return cb.equal(rolesJoin.get("name"), roleName);
        };
    }

    /**
     * Filtre par statut (enabled)
     */
    public static Specification<User> isEnabled(Boolean enabled) {
        return (root, query, cb) -> {
            if (enabled == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("enabled"), enabled);
        };
    }

    /**
     * Filtre par statut de compte (accountNonExpired)
     */
    public static Specification<User> isAccountNonExpired(Boolean accountNonExpired) {
        return (root, query, cb) -> {
            if (accountNonExpired == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("accountNonExpired"), accountNonExpired);
        };
    }

    /**
     * Filtre par statut de verrouillage (accountNonLocked)
     */
    public static Specification<User> isAccountNonLocked(Boolean accountNonLocked) {
        return (root, query, cb) -> {
            if (accountNonLocked == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("accountNonLocked"), accountNonLocked);
        };
    }

    /**
     * Filtre par statut de credentials (credentialsNonExpired)
     */
    public static Specification<User> isCredentialsNonExpired(Boolean credentialsNonExpired) {
        return (root, query, cb) -> {
            if (credentialsNonExpired == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("credentialsNonExpired"), credentialsNonExpired);
        };
    }

    /**
     * Filtre par statut combiné (ACTIVE, INACTIVE, SUSPENDED, etc.)
     * ACTIVE: enabled=true, accountNonLocked=true
     * INACTIVE: enabled=false
     * SUSPENDED: accountNonLocked=false
     */
    public static Specification<User> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty() || "all".equalsIgnoreCase(status)) {
                return cb.conjunction();
            }
            
            List<Predicate> predicates = new ArrayList<>();
            
            switch (status.toUpperCase()) {
                case "ACTIVE":
                    predicates.add(cb.equal(root.get("enabled"), true));
                    predicates.add(cb.equal(root.get("accountNonLocked"), true));
                    break;
                case "INACTIVE":
                    predicates.add(cb.equal(root.get("enabled"), false));
                    break;
                case "SUSPENDED":
                    predicates.add(cb.equal(root.get("accountNonLocked"), false));
                    break;
                case "PENDING":
                    // Pending pourrait être défini comme enabled=false mais accountNonLocked=true
                    predicates.add(cb.equal(root.get("enabled"), false));
                    predicates.add(cb.equal(root.get("accountNonLocked"), true));
                    break;
                default:
                    return cb.conjunction();
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Recherche par email, firstName ou lastName
     */
    public static Specification<User> searchByText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("email")), pattern),
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern)
            );
        };
    }

    /**
     * Combine plusieurs specifications avec AND
     */
    public static Specification<User> combine(Specification<User>... specifications) {
        Specification<User> combined = null;
        for (Specification<User> spec : specifications) {
            if (spec != null) {
                combined = combined == null ? spec : combined.and(spec);
            }
        }
        return combined != null ? combined : (root, query, cb) -> cb.conjunction();
    }
}

