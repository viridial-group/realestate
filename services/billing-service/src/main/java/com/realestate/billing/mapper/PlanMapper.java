package com.realestate.billing.mapper;

import com.realestate.billing.dto.PlanDTO;
import com.realestate.billing.entity.Plan;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

    public PlanDTO toDTO(Plan plan) {
        if (plan == null) {
            return null;
        }
        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setCurrency(plan.getCurrency());
        dto.setBillingPeriod(plan.getBillingPeriod());
        dto.setMaxProperties(plan.getMaxProperties());
        dto.setMaxUsers(plan.getMaxUsers());
        dto.setMaxStorageGb(plan.getMaxStorageGb());
        dto.setFeatures(plan.getFeatures());
        dto.setActive(plan.getActive());
        dto.setIsDefault(plan.getIsDefault());
        return dto;
    }

    public Plan toEntity(PlanDTO dto) {
        if (dto == null) {
            return null;
        }
        Plan plan = new Plan();
        plan.setId(dto.getId());
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setCurrency(dto.getCurrency());
        plan.setBillingPeriod(dto.getBillingPeriod());
        plan.setMaxProperties(dto.getMaxProperties());
        plan.setMaxUsers(dto.getMaxUsers());
        plan.setMaxStorageGb(dto.getMaxStorageGb());
        plan.setFeatures(dto.getFeatures());
        plan.setActive(dto.getActive());
        plan.setIsDefault(dto.getIsDefault());
        return plan;
    }
}

