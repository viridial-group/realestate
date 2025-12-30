package com.realestate.emailing.repository;

import com.realestate.emailing.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    List<EmailTemplate> findByType(String type);

    List<EmailTemplate> findByOrganizationId(Long organizationId);

    @Query("SELECT t FROM EmailTemplate t WHERE t.type = :type AND t.active = true AND (t.organizationId = :organizationId OR t.organizationId IS NULL) ORDER BY t.organizationId NULLS LAST, t.isDefault DESC")
    List<EmailTemplate> findActiveByTypeAndOrganization(
            @Param("type") String type,
            @Param("organizationId") Long organizationId
    );

    @Query("SELECT t FROM EmailTemplate t WHERE t.type = :type AND t.isDefault = true AND t.active = true AND (t.organizationId = :organizationId OR t.organizationId IS NULL) ORDER BY t.organizationId NULLS LAST")
    Optional<EmailTemplate> findDefaultByTypeAndOrganization(
            @Param("type") String type,
            @Param("organizationId") Long organizationId
    );

    @Query("SELECT t FROM EmailTemplate t WHERE t.name = :name AND t.active = true AND (t.organizationId = :organizationId OR t.organizationId IS NULL)")
    Optional<EmailTemplate> findActiveByNameAndOrganization(
            @Param("name") String name,
            @Param("organizationId") Long organizationId
    );
}

