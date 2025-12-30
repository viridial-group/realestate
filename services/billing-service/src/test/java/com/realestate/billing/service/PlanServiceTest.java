package com.realestate.billing.service;

import com.realestate.billing.entity.Plan;
import com.realestate.billing.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    private Plan testPlan;

    @BeforeEach
    void setUp() {
        testPlan = new Plan();
        testPlan.setId(1L);
        testPlan.setName("BASIC");
        testPlan.setDescription("Basic plan");
        testPlan.setPrice(new BigDecimal("29.99"));
        testPlan.setCurrency("EUR");
        testPlan.setBillingPeriod("MONTHLY");
        testPlan.setMaxProperties(100);
        testPlan.setMaxUsers(10);
        testPlan.setMaxStorageGb(10);
        testPlan.setActive(true);
        testPlan.setIsDefault(false);
    }

    @Test
    void testCreatePlan_Success() {
        // Given
        when(planRepository.save(any(Plan.class))).thenReturn(testPlan);

        // When
        Plan result = planService.createPlan(testPlan);

        // Then
        assertNotNull(result);
        assertEquals("BASIC", result.getName());
        verify(planRepository).save(testPlan);
    }

    @Test
    void testGetPlanById_Success() {
        // Given
        Long id = 1L;
        when(planRepository.findById(id)).thenReturn(Optional.of(testPlan));

        // When
        Optional<Plan> result = planService.getPlanById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPlan.getId(), result.get().getId());
        verify(planRepository).findById(id);
    }

    @Test
    void testGetAllActivePlans_Success() {
        // Given
        List<Plan> plans = Arrays.asList(testPlan);
        when(planRepository.findByActiveTrue()).thenReturn(plans);

        // When
        List<Plan> result = planService.getAllActivePlans();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(planRepository).findByActiveTrue();
    }

    @Test
    void testUpdatePlan_Success() {
        // Given
        Long id = 1L;
        Plan planDetails = new Plan();
        planDetails.setDescription("Updated description");
        planDetails.setPrice(new BigDecimal("39.99"));

        when(planRepository.findById(id)).thenReturn(Optional.of(testPlan));
        when(planRepository.save(any(Plan.class))).thenReturn(testPlan);

        // When
        Plan result = planService.updatePlan(id, planDetails);

        // Then
        assertNotNull(result);
        verify(planRepository).findById(id);
        verify(planRepository).save(any(Plan.class));
    }
}

