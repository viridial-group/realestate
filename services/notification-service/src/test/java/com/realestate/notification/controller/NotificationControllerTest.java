package com.realestate.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.notification.entity.Notification;
import com.realestate.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification createTestNotification() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setType("INFO");
        notification.setTitle("New Property Created");
        notification.setMessage("A new property has been created");
        notification.setRecipientId(10L);
        notification.setOrganizationId(100L);
        notification.setSenderId(5L);
        notification.setStatus("PENDING");
        notification.setChannel("IN_APP");
        notification.setTargetType("PROPERTY");
        notification.setTargetId(50L);
        notification.setActive(true);
        return notification;
    }

    @Test
    void testCreateNotification_Success() throws Exception {
        // Given
        Notification notification = createTestNotification();
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notification);

        // When & Then
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Property Created"))
                .andExpect(jsonPath("$.type").value("INFO"));
    }

    @Test
    void testSendNotification_Success() throws Exception {
        // Given
        Notification notification = createTestNotification();
        notification.setStatus("SENT");
        when(notificationService.sendNotification(
                eq("INFO"), eq("Title"), eq("Message"), eq(10L), eq(100L), 
                any(), eq("IN_APP"), eq("PROPERTY"), any()))
                .thenReturn(notification);

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"INFO\",\"title\":\"Title\",\"message\":\"Message\",\"recipientId\":10,\"organizationId\":100,\"channel\":\"IN_APP\",\"targetType\":\"PROPERTY\",\"targetId\":50}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void testGetNotificationById_Success() throws Exception {
        // Given
        Long id = 1L;
        Notification notification = createTestNotification();
        when(notificationService.getNotificationById(id)).thenReturn(Optional.of(notification));

        // When & Then
        mockMvc.perform(get("/api/notifications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Property Created"));
    }

    @Test
    void testGetNotifications_Success() throws Exception {
        // Given
        Long recipientId = 10L;
        List<Notification> notifications = Arrays.asList(createTestNotification());
        when(notificationService.getNotificationsByRecipientId(recipientId)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/api/notifications")
                        .param("recipientId", recipientId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetUnreadCount_Success() throws Exception {
        // Given
        Long recipientId = 10L;
        when(notificationService.getUnreadCountByRecipientId(recipientId)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/notifications/unread/count")
                        .param("recipientId", recipientId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        // Given
        Long id = 1L;
        Notification notification = createTestNotification();
        notification.setStatus("READ");
        when(notificationService.markAsRead(id)).thenReturn(notification);

        // When & Then
        mockMvc.perform(put("/api/notifications/{id}/read", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READ"));
    }

    @Test
    void testMarkAsArchived_Success() throws Exception {
        // Given
        Long id = 1L;
        Notification notification = createTestNotification();
        notification.setStatus("ARCHIVED");
        when(notificationService.markAsArchived(id)).thenReturn(notification);

        // When & Then
        mockMvc.perform(put("/api/notifications/{id}/archive", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }

    @Test
    void testUpdateNotification_Success() throws Exception {
        // Given
        Long id = 1L;
        Notification updateDetails = createTestNotification();
        updateDetails.setTitle("Updated Title");
        Notification updated = createTestNotification();
        updated.setTitle("Updated Title");

        when(notificationService.updateNotification(eq(id), any(Notification.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/notifications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testDeleteNotification_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(notificationService).deleteNotification(id);

        // When & Then
        mockMvc.perform(delete("/api/notifications/{id}", id))
                .andExpect(status().isNoContent());
    }
}

