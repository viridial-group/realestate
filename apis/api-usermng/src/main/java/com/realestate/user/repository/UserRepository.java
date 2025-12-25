package com.realestate.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.realestate.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByOrganizationId(UUID organizationId);
    
    Optional<User> findByEmail(String email);

    /**
     * Récupère un utilisateur par email avec son organisation (évite N+1)
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.organization WHERE u.email = :email")
    Optional<User> findByEmailWithOrganization(String email);

    /**
     * Récupère un utilisateur par ID avec toutes ses relations (évite N+1)
     * Charge l'organisation et les rôles en une seule requête
     */
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.organization
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions
        WHERE u.id = :id
        """)
    Optional<User> findByIdWithRelations(@org.springframework.data.repository.query.Param("id") UUID id);
    
    /**
     * Récupère un utilisateur par ID avec EntityGraph (alternative)
     */
    @EntityGraph(attributePaths = {"organization", "roles", "roles.permissions"})
    Optional<User> findWithGraphById(UUID id);

    /**
     * Récupère les utilisateurs par liste d'IDs d'organisations (optimisé)
     * Utilise JOIN FETCH pour éviter N+1 sur les relations
     */
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.organization
        LEFT JOIN FETCH u.roles
        WHERE u.organization.id IN :organizationIds
        """)
    List<User> findByOrganizationIdIn(@org.springframework.data.repository.query.Param("organizationIds") List<UUID> organizationIds);
    
    /**
     * Récupère les utilisateurs par liste d'IDs d'organisations avec EntityGraph
     */
    @EntityGraph(attributePaths = {"organization", "roles"})
    @Query("SELECT u FROM User u WHERE u.organization.id IN :organizationIds")
    List<User> findByOrganizationIdInWithGraph(@org.springframework.data.repository.query.Param("organizationIds") List<UUID> organizationIds);
}

