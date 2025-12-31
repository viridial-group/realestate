package com.realestate.common.event;

/**
 * Event published when a workflow task is completed (approved or rejected).
 */
public class WorkflowTaskCompletedEvent extends BaseEvent {
    
    private Long taskId;
    private Long workflowId;
    private String taskStatus; // APPROVED, REJECTED
    private String comments;
    private Long completedByUserId;
    private String targetType;
    private Long targetId;
    
    public WorkflowTaskCompletedEvent() {
        super();
    }
    
    public WorkflowTaskCompletedEvent(Long organizationId, Long userId, Long taskId, 
                                     Long workflowId, String taskStatus, String comments,
                                     Long completedByUserId, String targetType, Long targetId) {
        super(organizationId, userId);
        this.taskId = taskId;
        this.workflowId = workflowId;
        this.taskStatus = taskStatus;
        this.comments = comments;
        this.completedByUserId = completedByUserId;
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
    
    public String getTaskStatus() {
        return taskStatus;
    }
    
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public Long getCompletedByUserId() {
        return completedByUserId;
    }
    
    public void setCompletedByUserId(Long completedByUserId) {
        this.completedByUserId = completedByUserId;
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

