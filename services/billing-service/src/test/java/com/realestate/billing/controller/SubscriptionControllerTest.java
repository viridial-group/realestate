package com.realestate.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.billing.entity.Plan;
import com.realestate.billing.entity.Subscription;
import com.realestate.billing.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SubscriptionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Subscription createTestSubscription() {
        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("BASIC");
        plan.setPrice(new BigDecimal("29.99"));

        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setPlan(plan);
        subscription.setOrganizationId(100L);
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(LocalDateTime.now());
        return subscription;
    }

    @Test
    void testCreateSubscription_Success() throws Exception {
        // Given
        Subscription subscription = createTestSubscription();
        when(subscriptionService.createSubscription(anyLong(), anyLong())).thenReturn(subscription);

        // When & Then
        mockMvc.perform(post("/api/billing/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"planId\":1,\"organizationId\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetSubscriptionByOrganizationId_Success() throws Exception {
        // Given
        Long organizationId = 100L;
        Subscription subscription = createTestSubscription();
        when(subscriptionService.getSubscriptionByOrganizationId(organizationId)).thenReturn(Optional.of(subscription));

        // When & Then
        mockMvc.perform(get("/api/billing/subscriptions/organization/{organizationId}", organizationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizationId").value(100L));
    }

    @Test
    void testCancelSubscription_Success() throws Exception {
        // Given
        Long id = 1L;
        Subscription subscription = createTestSubscription();
        subscription.setStatus("CANCELLED");
        when(subscriptionService.cancelSubscription(eq(id), anyLong())).thenReturn(subscription);

        // When & Then
        mockMvc.perform(post("/api/billing/subscriptions/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cancelledBy\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}

