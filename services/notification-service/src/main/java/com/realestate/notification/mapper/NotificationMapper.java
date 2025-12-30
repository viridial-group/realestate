package com.realestate.notification.mapper;

import com.realestate.notification.dto.NotificationDTO;
import com.realestate.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRecipientId(notification.getRecipientId());
        dto.setOrganizationId(notification.getOrganizationId());
        dto.setSenderId(notification.getSenderId());
        dto.setStatus(notification.getStatus());
        dto.setChannel(notification.getChannel());
        dto.setTargetType(notification.getTargetType());
        dto.setTargetId(notification.getTargetId());
        dto.setActionUrl(notification.getActionUrl());
        dto.setReadAt(notification.getReadAt());
        dto.setActive(notification.getActive());
        dto.setMetadata(notification.getMetadata());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }

    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setType(dto.getType());
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setRecipientId(dto.getRecipientId());
        notification.setOrganizationId(dto.getOrganizationId());
        notification.setSenderId(dto.getSenderId());
        notification.setStatus(dto.getStatus());
        notification.setChannel(dto.getChannel());
        notification.setTargetType(dto.getTargetType());
        notification.setTargetId(dto.getTargetId());
        notification.setActionUrl(dto.getActionUrl());
        notification.setReadAt(dto.getReadAt());
        notification.setActive(dto.getActive());
        notification.setMetadata(dto.getMetadata());
        return notification;
    }
}

