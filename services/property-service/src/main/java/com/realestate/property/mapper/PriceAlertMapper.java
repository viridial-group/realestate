package com.realestate.property.mapper;

import com.realestate.property.dto.PriceAlertCreateDTO;
import com.realestate.property.dto.PriceAlertDTO;
import com.realestate.property.entity.PriceAlert;
import org.springframework.stereotype.Component;

@Component
public class PriceAlertMapper {

    public PriceAlertDTO toDTO(PriceAlert priceAlert) {
        if (priceAlert == null) {
            return null;
        }

        PriceAlertDTO dto = new PriceAlertDTO();
        dto.setId(priceAlert.getId());
        dto.setPropertyId(priceAlert.getPropertyId());
        dto.setUserId(priceAlert.getUserId());
        dto.setAlertType(priceAlert.getAlertType());
        dto.setTargetPrice(priceAlert.getTargetPrice());
        dto.setPercentageThreshold(priceAlert.getPercentageThreshold());
        dto.setIsPercentage(priceAlert.getIsPercentage());
        dto.setNotified(priceAlert.getNotified());
        dto.setNotifiedAt(priceAlert.getNotifiedAt());
        dto.setActive(priceAlert.getActive());
        dto.setEmailNotification(priceAlert.getEmailNotification());
        dto.setInAppNotification(priceAlert.getInAppNotification());
        dto.setCreatedAt(priceAlert.getCreatedAt());
        dto.setUpdatedAt(priceAlert.getUpdatedAt());
        // propertyTitle will be set by service

        return dto;
    }

    public PriceAlert toEntity(PriceAlertCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        PriceAlert priceAlert = new PriceAlert();
        priceAlert.setPropertyId(createDTO.getPropertyId());
        priceAlert.setAlertType(createDTO.getAlertType());
        priceAlert.setTargetPrice(createDTO.getTargetPrice());
        priceAlert.setPercentageThreshold(createDTO.getPercentageThreshold());
        
        // Déterminer si c'est une alerte basée sur un pourcentage
        boolean isPercentage = createDTO.getAlertType() != null && 
                              (createDTO.getAlertType().equals("PERCENTAGE_DROP") || 
                               createDTO.getAlertType().equals("PERCENTAGE_INCREASE"));
        priceAlert.setIsPercentage(isPercentage);
        
        priceAlert.setEmailNotification(createDTO.getEmailNotification() != null ? createDTO.getEmailNotification() : true);
        priceAlert.setInAppNotification(createDTO.getInAppNotification() != null ? createDTO.getInAppNotification() : true);
        priceAlert.setNotified(false);
        priceAlert.setActive(true);

        return priceAlert;
    }
}

