package com.realestate.billing.controller;

import com.realestate.billing.dto.PlanDTO;
import com.realestate.billing.entity.Plan;
import com.realestate.billing.mapper.PlanMapper;
import com.realestate.billing.service.PlanService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing/plans")
@Tag(name = "Plans", description = "Plan management API for subscription plans with features, quotas, and pricing")
public class PlanController {

    private final PlanService planService;
    private final PlanMapper planMapper;

    public PlanController(PlanService planService, PlanMapper planMapper) {
        this.planService = planService;
        this.planMapper = planMapper;
    }

    @PostMapping
    @Operation(summary = "Create plan", description = "Creates a new subscription plan")
    public ResponseEntity<PlanDTO> createPlan(@Valid @RequestBody PlanDTO planDTO) {
        Plan plan = planMapper.toEntity(planDTO);
        Plan created = planService.createPlan(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(planMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plan by ID", description = "Returns plan information for a specific plan ID")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable Long id) {
        Plan plan = planService.getPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", id));
        return ResponseEntity.ok(planMapper.toDTO(plan));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get plan by name", description = "Returns plan information for a specific plan name")
    public ResponseEntity<PlanDTO> getPlanByName(@PathVariable String name) {
        Plan plan = planService.getPlanByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", name));
        return ResponseEntity.ok(planMapper.toDTO(plan));
    }

    @GetMapping
    @Operation(summary = "List active plans", description = "Returns a list of all active subscription plans")
    public ResponseEntity<List<PlanDTO>> getAllActivePlans() {
        List<Plan> plans = planService.getAllActivePlans();
        List<PlanDTO> planDTOs = plans.stream()
                .map(planMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(planDTOs);
    }

    @GetMapping("/default")
    @Operation(summary = "Get default plan", description = "Returns the default subscription plan")
    public ResponseEntity<PlanDTO> getDefaultPlan() {
        Plan plan = planService.getDefaultPlan()
                .orElseThrow(() -> new ResourceNotFoundException("Default plan not found"));
        return ResponseEntity.ok(planMapper.toDTO(plan));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update plan", description = "Updates an existing subscription plan")
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable Long id, @Valid @RequestBody PlanDTO planDTO) {
        Plan plan = planMapper.toEntity(planDTO);
        Plan updated = planService.updatePlan(id, plan);
        return ResponseEntity.ok(planMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete plan", description = "Deletes a subscription plan")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}

