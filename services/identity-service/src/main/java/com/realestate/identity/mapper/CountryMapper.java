package com.realestate.identity.mapper;

import com.realestate.identity.dto.CityDTO;
import com.realestate.identity.dto.CountryDTO;
import com.realestate.identity.entity.City;
import com.realestate.identity.entity.Country;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CountryMapper {

    public CountryDTO toDTO(Country country) {
        return toDTO(country, false);
    }

    public CountryDTO toDTO(Country country, boolean includeCities) {
        if (country == null) {
            return null;
        }

        CountryDTO dto = new CountryDTO();
        dto.setId(country.getId());
        dto.setCode(country.getCode());
        dto.setName(country.getName());
        dto.setCode3(country.getCode3());
        dto.setPhoneCode(country.getPhoneCode());
        dto.setCurrency(country.getCurrency());
        dto.setCurrencySymbol(country.getCurrencySymbol());
        dto.setTimezone(country.getTimezone());
        dto.setFlagEmoji(country.getFlagEmoji());
        dto.setDescription(country.getDescription());
        dto.setLatitude(country.getLatitude());
        dto.setLongitude(country.getLongitude());
        dto.setImportantData(country.getImportantData());
        dto.setActive(country.getActive());
        dto.setDisplayOrder(country.getDisplayOrder());
        dto.setCreatedAt(country.getCreatedAt());
        dto.setUpdatedAt(country.getUpdatedAt());

        // Note: Les villes sont chargées séparément via CityService pour éviter les dépendances circulaires
        // Si includeCities est true, les villes doivent être chargées séparément

        return dto;
    }

    public Country toEntity(CountryDTO dto) {
        if (dto == null) {
            return null;
        }

        Country country = new Country();
        country.setId(dto.getId());
        country.setCode(dto.getCode());
        country.setName(dto.getName());
        country.setCode3(dto.getCode3());
        country.setPhoneCode(dto.getPhoneCode());
        country.setCurrency(dto.getCurrency());
        country.setCurrencySymbol(dto.getCurrencySymbol());
        country.setTimezone(dto.getTimezone());
        country.setFlagEmoji(dto.getFlagEmoji());
        country.setDescription(dto.getDescription());
        country.setLatitude(dto.getLatitude());
        country.setLongitude(dto.getLongitude());
        country.setImportantData(dto.getImportantData());
        country.setActive(dto.getActive());
        country.setDisplayOrder(dto.getDisplayOrder());

        return country;
    }
}

