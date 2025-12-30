package com.realestate.billing.service;

import com.realestate.billing.entity.Plan;
import com.realestate.billing.entity.Subscription;
import com.realestate.billing.repository.PlanRepository;
import com.realestate.billing.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Plan testPlan;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        testPlan = new Plan();
        testPlan.setId(1L);
        testPlan.setName("BASIC");
        testPlan.setPrice(new BigDecimal("29.99"));
        testPlan.setBillingPeriod("MONTHLY");

        testSubscription = new Subscription();
        testSubscription.setId(1L);
        testSubscription.setPlan(testPlan);
        testSubscription.setOrganizationId(100L);
        testSubscription.setStatus("ACTIVE");
        testSubscription.setStartDate(LocalDateTime.now());
    }

    @Test
    void testCreateSubscription_Success() {
        // Given
        Long planId = 1L;
        Long organizationId = 100L;
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(subscriptionRepository.findByOrganizationId(organizationId)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        Subscription result = subscriptionService.createSubscription(planId, organizationId);

        // Then
        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(planRepository).findById(planId);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void testGetSubscriptionByOrganizationId_Success() {
        // Given
        Long organizationId = 100L;
        when(subscriptionRepository.findByOrganizationId(organizationId)).thenReturn(Optional.of(testSubscription));

        // When
        Optional<Subscription> result = subscriptionService.getSubscriptionByOrganizationId(organizationId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSubscription.getOrganizationId(), result.get().getOrganizationId());
        verify(subscriptionRepository).findByOrganizationId(organizationId);
    }

    @Test
    void testCancelSubscription_Success() {
        // Given
        Long id = 1L;
        Long cancelledBy = 10L;
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(testSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        Subscription result = subscriptionService.cancelSubscription(id, cancelledBy);

        // Then
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        assertFalse(result.getActive());
        verify(subscriptionRepository).findById(id);
        verify(subscriptionRepository).save(any(Subscription.class));
    }
}

