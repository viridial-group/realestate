package com.realestate.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.notification.entity.NotificationSubscription;
import com.realestate.notification.service.NotificationSubscriptionService;
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

@WebMvcTest(controllers = NotificationSubscriptionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class NotificationSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationSubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationSubscription createTestSubscription() {
        NotificationSubscription subscription = new NotificationSubscription();
        subscription.setId(1L);
        subscription.setUserId(10L);
        subscription.setOrganizationId(100L);
        subscription.setNotificationType("PROPERTY_CREATED");
        subscription.setChannel("IN_APP");
        subscription.setEnabled(true);
        subscription.setActive(true);
        return subscription;
    }

    @Test
    void testCreateSubscription_Success() throws Exception {
        // Given
        NotificationSubscription subscription = createTestSubscription();
        when(subscriptionService.createSubscription(any(NotificationSubscription.class))).thenReturn(subscription);

        // When & Then
        mockMvc.perform(post("/api/notifications/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationType").value("PROPERTY_CREATED"));
    }

    @Test
    void testGetSubscriptionById_Success() throws Exception {
        // Given
        Long id = 1L;
        NotificationSubscription subscription = createTestSubscription();
        when(subscriptionService.getSubscriptionById(id)).thenReturn(Optional.of(subscription));

        // When & Then
        mockMvc.perform(get("/api/notifications/subscriptions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.notificationType").value("PROPERTY_CREATED"));
    }

    @Test
    void testGetSubscriptions_Success() throws Exception {
        // Given
        Long userId = 10L;
        List<NotificationSubscription> subscriptions = Arrays.asList(createTestSubscription());
        when(subscriptionService.getSubscriptionsByUserId(userId)).thenReturn(subscriptions);

        // When & Then
        mockMvc.perform(get("/api/notifications/subscriptions")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateSubscription_Success() throws Exception {
        // Given
        Long id = 1L;
        NotificationSubscription updateDetails = createTestSubscription();
        updateDetails.setChannel("PUSH");
        NotificationSubscription updated = createTestSubscription();
        updated.setChannel("PUSH");

        when(subscriptionService.updateSubscription(eq(id), any(NotificationSubscription.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/notifications/subscriptions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channel").value("PUSH"));
    }

    @Test
    void testDeleteSubscription_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(subscriptionService).deleteSubscription(id);

        // When & Then
        mockMvc.perform(delete("/api/notifications/subscriptions/{id}", id))
                .andExpect(status().isNoContent());
    }
}

