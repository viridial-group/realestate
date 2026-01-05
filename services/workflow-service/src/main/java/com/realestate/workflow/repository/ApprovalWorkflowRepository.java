package com.realestate.workflow.repository;

import com.realestate.workflow.entity.ApprovalWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long>, JpaSpecificationExecutor<ApprovalWorkflow> {

    List<ApprovalWorkflow> findByOrganizationId(Long organizationId);

    List<ApprovalWorkflow> findByAction(String action);

    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.organizationId = :organizationId AND w.action = :action AND w.active = true")
    List<ApprovalWorkflow> findActiveByOrganizationIdAndAction(
            @Param("organizationId") Long organizationId,
            @Param("action") String action
    );

    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.organizationId = :organizationId AND w.action = :action AND w.isDefault = true AND w.active = true")
    Optional<ApprovalWorkflow> findDefaultByOrganizationIdAndAction(
            @Param("organizationId") Long organizationId,
            @Param("action") String action
    );

    @Query("SELECT w FROM ApprovalWorkflow w WHERE w.targetType = :targetType AND w.targetId = :targetId AND w.active = true")
    List<ApprovalWorkflow> findActiveByTarget(
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId
    );
}

