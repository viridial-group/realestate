package com.realestate.billing.mapper;

import com.realestate.billing.dto.SubscriptionDTO;
import com.realestate.billing.entity.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public SubscriptionDTO toDTO(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());
        if (subscription.getPlan() != null) {
            dto.setPlanId(subscription.getPlan().getId());
            dto.setPlanName(subscription.getPlan().getName());
        }
        dto.setOrganizationId(subscription.getOrganizationId());
        dto.setStatus(subscription.getStatus());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setTrialEndDate(subscription.getTrialEndDate());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setCancelledAt(subscription.getCancelledAt());
        dto.setCancelledBy(subscription.getCancelledBy());
        dto.setActive(subscription.getActive());
        return dto;
    }
}

