package com.realestate.user.organization.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.realestate.user.organization.entity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    List<Organization> findByParentId(UUID parentId);
    
    @Query(value = """
        WITH RECURSIVE org_tree AS (
            SELECT id, parent_id FROM organizations WHERE id = :rootId
            UNION ALL
            SELECT o.id, o.parent_id
            FROM organizations o
            JOIN org_tree ot ON o.parent_id = ot.id
        )
        SELECT id FROM org_tree
        """, nativeQuery = true)
    List<UUID> findSubTreeIds(UUID rootId);

    /**
     * Récupère les organisations par liste d'IDs (optimisé)
     */
    @Query("SELECT o FROM Organization o WHERE o.id IN :ids")
    List<Organization> findByIdIn(@org.springframework.data.repository.query.Param("ids") List<UUID> ids);
}

