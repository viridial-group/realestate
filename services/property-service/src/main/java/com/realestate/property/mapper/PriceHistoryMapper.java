package com.realestate.property.mapper;

import com.realestate.property.dto.PriceHistoryCreateDTO;
import com.realestate.property.dto.PriceHistoryDTO;
import com.realestate.property.entity.PriceHistory;
import org.springframework.stereotype.Component;

@Component
public class PriceHistoryMapper {

    public PriceHistoryDTO toDTO(PriceHistory priceHistory) {
        if (priceHistory == null) {
            return null;
        }

        PriceHistoryDTO dto = new PriceHistoryDTO();
        dto.setId(priceHistory.getId());
        dto.setPropertyId(priceHistory.getPropertyId());
        dto.setPrice(priceHistory.getPrice());
        dto.setCurrency(priceHistory.getCurrency());
        dto.setChangeDate(priceHistory.getChangeDate());
        dto.setChangeReason(priceHistory.getChangeReason());
        dto.setCreatedBy(priceHistory.getCreatedBy());
        dto.setSource(priceHistory.getSource());
        dto.setCreatedAt(priceHistory.getCreatedAt());
        dto.setUpdatedAt(priceHistory.getUpdatedAt());

        return dto;
    }

    public PriceHistory toEntity(PriceHistoryCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setPropertyId(createDTO.getPropertyId());
        priceHistory.setPrice(createDTO.getPrice());
        priceHistory.setCurrency(createDTO.getCurrency() != null ? createDTO.getCurrency() : "EUR");
        priceHistory.setChangeDate(createDTO.getChangeDate() != null ? createDTO.getChangeDate() : java.time.LocalDateTime.now());
        priceHistory.setChangeReason(createDTO.getChangeReason());
        priceHistory.setSource(createDTO.getSource() != null ? createDTO.getSource() : "MANUAL");

        return priceHistory;
    }
}

