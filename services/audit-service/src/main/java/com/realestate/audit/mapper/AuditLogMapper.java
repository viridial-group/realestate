package com.realestate.audit.mapper;

import com.realestate.audit.dto.AuditLogDTO;
import com.realestate.audit.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogDTO toDTO(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(auditLog.getId());
        dto.setActorId(auditLog.getActorId());
        dto.setActorEmail(auditLog.getActorEmail());
        dto.setOrganizationId(auditLog.getOrganizationId());
        dto.setAction(auditLog.getAction());
        dto.setTargetType(auditLog.getTargetType());
        dto.setTargetId(auditLog.getTargetId());
        dto.setStatus(auditLog.getStatus());
        dto.setDescription(auditLog.getDescription());
        dto.setIpAddress(auditLog.getIpAddress());
        dto.setUserAgent(auditLog.getUserAgent());
        dto.setRequestMethod(auditLog.getRequestMethod());
        dto.setRequestPath(auditLog.getRequestPath());
        dto.setMetadata(auditLog.getMetadata());
        dto.setErrorMessage(auditLog.getErrorMessage());
        dto.setCreatedAt(auditLog.getCreatedAt());
        return dto;
    }

    public AuditLog toEntity(AuditLogDTO dto) {
        if (dto == null) {
            return null;
        }
        AuditLog auditLog = new AuditLog();
        auditLog.setId(dto.getId());
        auditLog.setActorId(dto.getActorId());
        auditLog.setActorEmail(dto.getActorEmail());
        auditLog.setOrganizationId(dto.getOrganizationId());
        auditLog.setAction(dto.getAction());
        auditLog.setTargetType(dto.getTargetType());
        auditLog.setTargetId(dto.getTargetId());
        auditLog.setStatus(dto.getStatus());
        auditLog.setDescription(dto.getDescription());
        auditLog.setIpAddress(dto.getIpAddress());
        auditLog.setUserAgent(dto.getUserAgent());
        auditLog.setRequestMethod(dto.getRequestMethod());
        auditLog.setRequestPath(dto.getRequestPath());
        auditLog.setMetadata(dto.getMetadata());
        auditLog.setErrorMessage(dto.getErrorMessage());
        return auditLog;
    }
}

