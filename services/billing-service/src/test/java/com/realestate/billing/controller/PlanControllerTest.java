package com.realestate.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.billing.entity.Plan;
import com.realestate.billing.service.PlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PlanController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    @Autowired
    private ObjectMapper objectMapper;

    private Plan createTestPlan() {
        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("BASIC");
        plan.setDescription("Basic plan");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setCurrency("EUR");
        plan.setBillingPeriod("MONTHLY");
        plan.setMaxProperties(100);
        plan.setMaxUsers(10);
        plan.setActive(true);
        return plan;
    }

    @Test
    void testCreatePlan_Success() throws Exception {
        // Given
        Plan plan = createTestPlan();
        when(planService.createPlan(any(Plan.class))).thenReturn(plan);

        // When & Then
        mockMvc.perform(post("/api/billing/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("BASIC"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    void testGetPlanById_Success() throws Exception {
        // Given
        Long id = 1L;
        Plan plan = createTestPlan();
        when(planService.getPlanById(id)).thenReturn(Optional.of(plan));

        // When & Then
        mockMvc.perform(get("/api/billing/plans/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("BASIC"));
    }

    @Test
    void testGetAllActivePlans_Success() throws Exception {
        // Given
        List<Plan> plans = Arrays.asList(createTestPlan());
        when(planService.getAllActivePlans()).thenReturn(plans);

        // When & Then
        mockMvc.perform(get("/api/billing/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}

