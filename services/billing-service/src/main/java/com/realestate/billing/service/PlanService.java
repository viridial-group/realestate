package com.realestate.billing.service;

import com.realestate.billing.entity.Plan;
import com.realestate.billing.repository.PlanRepository;
import com.realestate.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public Plan createPlan(Plan plan) {
        // Si c'est le plan par défaut, désactiver les autres
        if (plan.getIsDefault() != null && plan.getIsDefault()) {
            planRepository.findByIsDefaultTrue().ifPresent(existingDefault -> {
                existingDefault.setIsDefault(false);
                planRepository.save(existingDefault);
            });
        }
        return planRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public Optional<Plan> getPlanById(Long id) {
        return planRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Plan> getPlanByName(String name) {
        return planRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Plan> getAllActivePlans() {
        return planRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Plan> getDefaultPlan() {
        return planRepository.findByIsDefaultTrue();
    }

    @Transactional
    public Plan updatePlan(Long id, Plan planDetails) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", id));

        if (planDetails.getName() != null) {
            plan.setName(planDetails.getName());
        }
        if (planDetails.getDescription() != null) {
            plan.setDescription(planDetails.getDescription());
        }
        if (planDetails.getPrice() != null) {
            plan.setPrice(planDetails.getPrice());
        }
        if (planDetails.getCurrency() != null) {
            plan.setCurrency(planDetails.getCurrency());
        }
        if (planDetails.getBillingPeriod() != null) {
            plan.setBillingPeriod(planDetails.getBillingPeriod());
        }
        if (planDetails.getMaxProperties() != null) {
            plan.setMaxProperties(planDetails.getMaxProperties());
        }
        if (planDetails.getMaxUsers() != null) {
            plan.setMaxUsers(planDetails.getMaxUsers());
        }
        if (planDetails.getMaxStorageGb() != null) {
            plan.setMaxStorageGb(planDetails.getMaxStorageGb());
        }
        if (planDetails.getFeatures() != null) {
            plan.setFeatures(planDetails.getFeatures());
        }
        if (planDetails.getActive() != null) {
            plan.setActive(planDetails.getActive());
        }
        if (planDetails.getIsDefault() != null) {
            // Si on définit un nouveau plan par défaut, désactiver l'ancien
            if (planDetails.getIsDefault()) {
                planRepository.findByIsDefaultTrue().ifPresent(existingDefault -> {
                    if (!existingDefault.getId().equals(id)) {
                        existingDefault.setIsDefault(false);
                        planRepository.save(existingDefault);
                    }
                });
            }
            plan.setIsDefault(planDetails.getIsDefault());
        }

        return planRepository.save(plan);
    }

    @Transactional
    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }
}

