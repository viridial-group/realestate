package com.realestate.notification.controller;

import com.realestate.notification.dto.NotificationDTO;
import com.realestate.notification.entity.Notification;
import com.realestate.notification.mapper.NotificationMapper;
import com.realestate.notification.service.NotificationService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Real-time notification management API for push, in-app, and SMS notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @PostMapping
    @Operation(summary = "Create notification", description = "Creates a new notification")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        Notification notification = notificationMapper.toEntity(notificationDTO);
        Notification created = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationMapper.toDTO(created));
    }

    @PostMapping("/send")
    @Operation(summary = "Send notification", description = "Sends a notification to a recipient")
    public ResponseEntity<NotificationDTO> sendNotification(@RequestBody Map<String, Object> request) {
        try {
            String type = request.get("type").toString();
            String title = request.get("title").toString();
            String message = request.get("message").toString();
            Long recipientId = Long.valueOf(request.get("recipientId").toString());
            Long organizationId = Long.valueOf(request.get("organizationId").toString());
            Long senderId = request.containsKey("senderId") ? Long.valueOf(request.get("senderId").toString()) : null;
            String channel = request.containsKey("channel") ? request.get("channel").toString() : "IN_APP";
            String targetType = request.containsKey("targetType") ? request.get("targetType").toString() : null;
            Long targetId = request.containsKey("targetId") ? Long.valueOf(request.get("targetId").toString()) : null;

            Notification sent = notificationService.sendNotification(
                    type, title, message, recipientId, organizationId, senderId, channel, targetType, targetId);
            return ResponseEntity.status(HttpStatus.CREATED).body(notificationMapper.toDTO(sent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Returns notification information for a specific notification ID")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        return ResponseEntity.ok(notificationMapper.toDTO(notification));
    }

    @GetMapping
    @Operation(summary = "List notifications", description = "Returns a list of notifications for a recipient")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestParam Long recipientId,
            @RequestParam(required = false) Boolean unread) {
        List<Notification> notifications;

        if (unread != null && unread) {
            notifications = notificationService.getUnreadNotificationsByRecipientId(recipientId);
        } else {
            notifications = notificationService.getNotificationsByRecipientId(recipientId);
        }

        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notificationDTOs);
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Returns the count of unread notifications for a recipient")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam Long recipientId) {
        Long count = notificationService.getUnreadCountByRecipientId(recipientId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a notification as read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        Notification updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(notificationMapper.toDTO(updated));
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Archive notification", description = "Archives a notification")
    public ResponseEntity<NotificationDTO> markAsArchived(@PathVariable Long id) {
        Notification updated = notificationService.markAsArchived(id);
        return ResponseEntity.ok(notificationMapper.toDTO(updated));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update notification", description = "Updates notification information for a specific notification ID")
    public ResponseEntity<NotificationDTO> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationDTO notificationDTO) {
        Notification notification = notificationMapper.toEntity(notificationDTO);
        Notification updated = notificationService.updateNotification(id, notification);
        return ResponseEntity.ok(notificationMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Deletes a notification from the database by ID")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
