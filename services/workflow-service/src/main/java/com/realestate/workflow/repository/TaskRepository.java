package com.realestate.workflow.repository;

import com.realestate.workflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByWorkflowId(Long workflowId);

    List<Task> findByAssignedTo(Long assignedTo);

    List<Task> findByOrganizationId(Long organizationId);

    @Query("SELECT t FROM Task t WHERE t.workflow.id = :workflowId AND t.active = true ORDER BY t.stepNumber ASC")
    List<Task> findActiveByWorkflowIdOrderByStepNumber(@Param("workflowId") Long workflowId);

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :assignedTo AND t.status = :status AND t.active = true")
    List<Task> findActiveByAssignedToAndStatus(
            @Param("assignedTo") Long assignedTo,
            @Param("status") String status
    );

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :assignedTo AND t.status IN ('PENDING', 'IN_PROGRESS') AND t.active = true")
    List<Task> findPendingTasksByAssignedTo(@Param("assignedTo") Long assignedTo);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status IN ('PENDING', 'IN_PROGRESS') AND t.active = true")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.workflow.id = :workflowId AND t.stepNumber = :stepNumber AND t.active = true")
    Optional<Task> findActiveByWorkflowIdAndStepNumber(
            @Param("workflowId") Long workflowId,
            @Param("stepNumber") Integer stepNumber
    );
}

