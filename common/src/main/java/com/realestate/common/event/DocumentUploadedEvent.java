package com.realestate.common.event;

/**
 * Event published when a document is uploaded.
 */
public class DocumentUploadedEvent extends BaseEvent {
    
    private Long documentId;
    private String documentName;
    private String documentType;
    private Long fileSize;
    private String targetType; // PROPERTY, RESOURCE, etc.
    private Long targetId;
    
    public DocumentUploadedEvent() {
        super();
    }
    
    public DocumentUploadedEvent(Long organizationId, Long userId, Long documentId, 
                                String documentName, String documentType, Long fileSize,
                                String targetType, Long targetId) {
        super(organizationId, userId);
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentType = documentType;
        this.fileSize = fileSize;
        this.targetType = targetType;
        this.targetId = targetId;
    }
    
    // Getters and Setters
    public Long getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
    
    public String getDocumentName() {
        return documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

