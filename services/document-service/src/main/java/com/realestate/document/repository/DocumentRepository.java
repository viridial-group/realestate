package com.realestate.document.repository;

import com.realestate.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    List<Document> findByOrganizationId(Long organizationId);

    List<Document> findByPropertyId(Long propertyId);

    List<Document> findByResourceId(Long resourceId);

    @Query("SELECT d FROM Document d WHERE d.organizationId = :organizationId AND d.active = true")
    List<Document> findActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT d FROM Document d WHERE d.propertyId = :propertyId AND d.active = true")
    List<Document> findActiveByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT d FROM Document d WHERE d.resourceId = :resourceId AND d.active = true")
    List<Document> findActiveByResourceId(@Param("resourceId") Long resourceId);

    @Query("SELECT d FROM Document d WHERE d.type = :type AND d.active = true")
    List<Document> findActiveByType(@Param("type") String type);

    @Query("SELECT d FROM Document d WHERE d.organizationId = :organizationId AND d.type = :type AND d.active = true")
    List<Document> findActiveByOrganizationIdAndType(
            @Param("organizationId") Long organizationId,
            @Param("type") String type
    );
}

