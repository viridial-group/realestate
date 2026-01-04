package com.realestate.identity.mapper;

import com.realestate.identity.dto.CityDTO;
import com.realestate.identity.entity.City;
import com.realestate.identity.entity.Country;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityDTO toDTO(City city) {
        if (city == null) {
            return null;
        }

        CityDTO dto = new CityDTO();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setPostalCode(city.getPostalCode());
        
        if (city.getCountry() != null) {
            dto.setCountryId(city.getCountry().getId());
            dto.setCountryCode(city.getCountry().getCode());
            dto.setCountryName(city.getCountry().getName());
        }
        
        dto.setLatitude(city.getLatitude());
        dto.setLongitude(city.getLongitude());
        dto.setRegion(city.getRegion());
        dto.setDepartment(city.getDepartment());
        dto.setTimezone(city.getTimezone());
        dto.setDescription(city.getDescription());
        dto.setImportantData(city.getImportantData());
        dto.setActive(city.getActive());
        dto.setDisplayOrder(city.getDisplayOrder());
        dto.setCreatedAt(city.getCreatedAt());
        dto.setUpdatedAt(city.getUpdatedAt());

        return dto;
    }

    public City toEntity(CityDTO dto, Country country) {
        if (dto == null) {
            return null;
        }

        City city = new City();
        city.setId(dto.getId());
        city.setName(dto.getName());
        city.setPostalCode(dto.getPostalCode());
        city.setCountry(country);
        city.setLatitude(dto.getLatitude());
        city.setLongitude(dto.getLongitude());
        city.setRegion(dto.getRegion());
        city.setDepartment(dto.getDepartment());
        city.setTimezone(dto.getTimezone());
        city.setDescription(dto.getDescription());
        city.setImportantData(dto.getImportantData());
        city.setActive(dto.getActive());
        city.setDisplayOrder(dto.getDisplayOrder());

        return city;
    }
}

