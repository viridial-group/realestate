package com.realestate.common.event;

/**
 * Event published when a workflow task is created.
 */
public class WorkflowTaskCreatedEvent extends BaseEvent {
    
    private Long taskId;
    private Long workflowId;
    private String taskType;
    private String taskStatus;
    private Long assignedUserId;
    private String assignedRole;
    private String targetType; // PROPERTY, RESOURCE, DOCUMENT, etc.
    private Long targetId;
    
    public WorkflowTaskCreatedEvent() {
        super();
    }
    
    public WorkflowTaskCreatedEvent(Long organizationId, Long userId, Long taskId, 
                                   Long workflowId, String taskType, String taskStatus,
                                   Long assignedUserId, String assignedRole,
                                   String targetType, Long targetId) {
        super(organizationId, userId);
        this.taskId = taskId;
        this.workflowId = workflowId;
        this.taskType = taskType;
        this.taskStatus = taskStatus;
        this.assignedUserId = assignedUserId;
        this.assignedRole = assignedRole;
        this.targetType = targetType;
        this.targetId = targetId;
    }
    
    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public Long getWorkflowId() {
        return workflowId;
    }
    
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }
    
    public String getTaskType() {
        return taskType;
    }
    
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    
    public String getTaskStatus() {
        return taskStatus;
    }
    
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
    
    public Long getAssignedUserId() {
        return assignedUserId;
    }
    
    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }
    
    public String getAssignedRole() {
        return assignedRole;
    }
    
    public void setAssignedRole(String assignedRole) {
        this.assignedRole = assignedRole;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}

