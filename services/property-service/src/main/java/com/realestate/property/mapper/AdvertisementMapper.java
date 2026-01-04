package com.realestate.property.mapper;

import com.realestate.property.dto.AdvertisementCreateDTO;
import com.realestate.property.dto.AdvertisementDTO;
import com.realestate.property.dto.AdvertisementUpdateDTO;
import com.realestate.property.entity.Advertisement;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class AdvertisementMapper {

    public AdvertisementDTO toDTO(Advertisement advertisement) {
        if (advertisement == null) {
            return null;
        }

        AdvertisementDTO dto = new AdvertisementDTO();
        dto.setId(advertisement.getId());
        dto.setOrganizationId(advertisement.getOrganizationId());
        dto.setTitle(advertisement.getTitle());
        dto.setDescription(advertisement.getDescription());
        dto.setImageUrl(advertisement.getImageUrl());
        dto.setLinkUrl(advertisement.getLinkUrl());
        dto.setAdType(advertisement.getAdType());
        dto.setPosition(advertisement.getPosition());
        dto.setStatus(advertisement.getStatus());
        dto.setStartDate(advertisement.getStartDate());
        dto.setEndDate(advertisement.getEndDate());
        dto.setBudget(advertisement.getBudget());
        dto.setDailyBudget(advertisement.getDailyBudget());
        dto.setCostPerClick(advertisement.getCostPerClick());
        dto.setCostPerImpression(advertisement.getCostPerImpression());
        dto.setTargetLocations(advertisement.getTargetLocations());
        dto.setTargetPropertyTypes(advertisement.getTargetPropertyTypes());
        dto.setTargetTransactionTypes(advertisement.getTargetTransactionTypes());
        dto.setImpressions(advertisement.getImpressions());
        dto.setClicks(advertisement.getClicks());
        dto.setConversions(advertisement.getConversions());
        dto.setTotalSpent(advertisement.getTotalSpent());
        dto.setMaxImpressionsPerDay(advertisement.getMaxImpressionsPerDay());
        dto.setPriority(advertisement.getPriority());
        dto.setActive(advertisement.getActive());
        dto.setCreatedAt(advertisement.getCreatedAt());
        dto.setUpdatedAt(advertisement.getUpdatedAt());
        dto.setCreatedBy(advertisement.getCreatedBy());

        // Calculer les métriques
        if (advertisement.getImpressions() != null && advertisement.getImpressions() > 0) {
            double ctr = (advertisement.getClicks() != null ? advertisement.getClicks().doubleValue() : 0.0) 
                         / advertisement.getImpressions().doubleValue() * 100.0;
            dto.setClickThroughRate(Math.round(ctr * 100.0) / 100.0);
        }

        if (advertisement.getClicks() != null && advertisement.getClicks() > 0) {
            double cr = (advertisement.getConversions() != null ? advertisement.getConversions().doubleValue() : 0.0) 
                       / advertisement.getClicks().doubleValue() * 100.0;
            dto.setConversionRate(Math.round(cr * 100.0) / 100.0);
        }

        if (advertisement.getConversions() != null && advertisement.getConversions() > 0 
            && advertisement.getTotalSpent() != null) {
            BigDecimal cpa = advertisement.getTotalSpent()
                .divide(BigDecimal.valueOf(advertisement.getConversions()), 2, RoundingMode.HALF_UP);
            dto.setCostPerConversion(cpa);
        }

        return dto;
    }

    public Advertisement toEntity(AdvertisementCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Advertisement advertisement = new Advertisement();
        advertisement.setOrganizationId(dto.getOrganizationId());
        advertisement.setTitle(dto.getTitle());
        advertisement.setDescription(dto.getDescription());
        advertisement.setImageUrl(dto.getImageUrl());
        advertisement.setLinkUrl(dto.getLinkUrl());
        advertisement.setAdType(dto.getAdType());
        advertisement.setPosition(dto.getPosition());
        advertisement.setStatus(dto.getStatus() != null ? dto.getStatus() : "DRAFT");
        advertisement.setStartDate(dto.getStartDate());
        advertisement.setEndDate(dto.getEndDate());
        advertisement.setBudget(dto.getBudget());
        advertisement.setDailyBudget(dto.getDailyBudget());
        advertisement.setCostPerClick(dto.getCostPerClick());
        advertisement.setCostPerImpression(dto.getCostPerImpression());
        advertisement.setTargetLocations(dto.getTargetLocations());
        advertisement.setTargetPropertyTypes(dto.getTargetPropertyTypes());
        advertisement.setTargetTransactionTypes(dto.getTargetTransactionTypes());
        advertisement.setMaxImpressionsPerDay(dto.getMaxImpressionsPerDay());
        advertisement.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        advertisement.setActive(dto.getActive() != null ? dto.getActive() : true);
        advertisement.setCreatedBy(dto.getCreatedBy());

        return advertisement;
    }

    public void updateEntity(Advertisement advertisement, AdvertisementUpdateDTO dto) {
        if (dto == null || advertisement == null) {
            return;
        }

        if (dto.getTitle() != null) {
            advertisement.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            advertisement.setDescription(dto.getDescription());
        }
        if (dto.getImageUrl() != null) {
            advertisement.setImageUrl(dto.getImageUrl());
        }
        if (dto.getLinkUrl() != null) {
            advertisement.setLinkUrl(dto.getLinkUrl());
        }
        if (dto.getAdType() != null) {
            advertisement.setAdType(dto.getAdType());
        }
        if (dto.getPosition() != null) {
            advertisement.setPosition(dto.getPosition());
        }
        if (dto.getStatus() != null) {
            advertisement.setStatus(dto.getStatus());
        }
        if (dto.getStartDate() != null) {
            advertisement.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            advertisement.setEndDate(dto.getEndDate());
        }
        if (dto.getBudget() != null) {
            advertisement.setBudget(dto.getBudget());
        }
        if (dto.getDailyBudget() != null) {
            advertisement.setDailyBudget(dto.getDailyBudget());
        }
        if (dto.getCostPerClick() != null) {
            advertisement.setCostPerClick(dto.getCostPerClick());
        }
        if (dto.getCostPerImpression() != null) {
            advertisement.setCostPerImpression(dto.getCostPerImpression());
        }
        if (dto.getTargetLocations() != null) {
            advertisement.setTargetLocations(dto.getTargetLocations());
        }
        if (dto.getTargetPropertyTypes() != null) {
            advertisement.setTargetPropertyTypes(dto.getTargetPropertyTypes());
        }
        if (dto.getTargetTransactionTypes() != null) {
            advertisement.setTargetTransactionTypes(dto.getTargetTransactionTypes());
        }
        if (dto.getMaxImpressionsPerDay() != null) {
            advertisement.setMaxImpressionsPerDay(dto.getMaxImpressionsPerDay());
        }
        if (dto.getPriority() != null) {
            advertisement.setPriority(dto.getPriority());
        }
        if (dto.getActive() != null) {
            advertisement.setActive(dto.getActive());
        }
    }
}

