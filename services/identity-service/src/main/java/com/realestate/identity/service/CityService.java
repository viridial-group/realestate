package com.realestate.identity.service;

import com.realestate.identity.dto.CityDTO;
import com.realestate.identity.entity.City;
import com.realestate.identity.entity.Country;
import com.realestate.identity.mapper.CityMapper;
import com.realestate.identity.repository.CityRepository;
import com.realestate.identity.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CityService {

    private static final Logger logger = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CountryRepository countryRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.cityMapper = cityMapper;
    }

    /**
     * Récupère toutes les villes actives (avec cache)
     */
    @Cacheable(value = "cities", key = "'all-active'")
    @Transactional(readOnly = true)
    public List<CityDTO> getAllActiveCities() {
        logger.debug("Fetching all active cities from database");
        List<City> cities = cityRepository.findAllActiveOrdered();
        return cities.stream()
                .map(cityMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les villes actives d'un pays (avec cache)
     */
    @Cacheable(value = "citiesByCountry", key = "#countryCode")
    @Transactional(readOnly = true)
    public List<CityDTO> getCitiesByCountryCode(String countryCode) {
        logger.debug("Fetching cities for country code: {}", countryCode);
        List<City> cities = cityRepository.findByCountryCodeAndActiveTrueOrderByDisplayOrderAscNameAsc(countryCode);
        return cities.stream()
                .map(cityMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les villes actives d'un pays par ID (avec cache)
     */
    @Cacheable(value = "citiesByCountry", key = "'country-id-' + #countryId")
    @Transactional(readOnly = true)
    public List<CityDTO> getCitiesByCountryId(Long countryId) {
        logger.debug("Fetching cities for country id: {}", countryId);
        List<City> cities = cityRepository.findActiveByCountryIdOrdered(countryId);
        return cities.stream()
                .map(cityMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une ville par son ID (avec cache)
     */
    @Cacheable(value = "city", key = "#id")
    @Transactional(readOnly = true)
    public CityDTO getCityById(Long id) {
        logger.debug("Fetching city with id: {}", id);
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));
        return cityMapper.toDTO(city);
    }

    /**
     * Crée une nouvelle ville (invalide le cache)
     */
    @CacheEvict(value = {"cities", "city", "citiesByCountry", "countries", "country"}, allEntries = true)
    public CityDTO createCity(CityDTO cityDTO) {
        logger.info("Creating new city: {}", cityDTO.getName());
        
        Country country = countryRepository.findById(cityDTO.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + cityDTO.getCountryId()));

        // Vérifier si la ville existe déjà pour ce pays
        if (cityRepository.existsByNameAndCountryCode(cityDTO.getName(), country.getCode())) {
            throw new RuntimeException("City " + cityDTO.getName() + " already exists in country " + country.getCode());
        }

        City city = cityMapper.toEntity(cityDTO, country);
        city = cityRepository.save(city);
        
        logger.info("City created successfully with id: {}", city.getId());
        return cityMapper.toDTO(city);
    }

    /**
     * Met à jour une ville (invalide le cache)
     */
    @CacheEvict(value = {"cities", "city", "citiesByCountry", "countries", "country"}, allEntries = true)
    public CityDTO updateCity(Long id, CityDTO cityDTO) {
        logger.info("Updating city with id: {}", id);
        
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));

        Country country = countryRepository.findById(cityDTO.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + cityDTO.getCountryId()));

        // Vérifier si le nom change et s'il existe déjà pour ce pays
        if (!city.getName().equals(cityDTO.getName()) && 
            cityRepository.existsByNameAndCountryCode(cityDTO.getName(), country.getCode())) {
            throw new RuntimeException("City " + cityDTO.getName() + " already exists in country " + country.getCode());
        }

        // Mettre à jour les champs
        city.setName(cityDTO.getName());
        city.setPostalCode(cityDTO.getPostalCode());
        city.setCountry(country);
        city.setLatitude(cityDTO.getLatitude());
        city.setLongitude(cityDTO.getLongitude());
        city.setRegion(cityDTO.getRegion());
        city.setDepartment(cityDTO.getDepartment());
        city.setTimezone(cityDTO.getTimezone());
        city.setDescription(cityDTO.getDescription());
        city.setImportantData(cityDTO.getImportantData());
        city.setActive(cityDTO.getActive());
        city.setDisplayOrder(cityDTO.getDisplayOrder());

        city = cityRepository.save(city);
        
        logger.info("City updated successfully with id: {}", city.getId());
        return cityMapper.toDTO(city);
    }

    /**
     * Supprime une ville (invalide le cache)
     */
    @CacheEvict(value = {"cities", "city", "citiesByCountry", "countries", "country"}, allEntries = true)
    public void deleteCity(Long id) {
        logger.info("Deleting city with id: {}", id);
        
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));

        cityRepository.delete(city);
        logger.info("City deleted successfully with id: {}", id);
    }
}

