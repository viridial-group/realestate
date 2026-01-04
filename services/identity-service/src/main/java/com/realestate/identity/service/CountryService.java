package com.realestate.identity.service;

import com.realestate.identity.dto.CountryDTO;
import com.realestate.identity.entity.Country;
import com.realestate.identity.mapper.CountryMapper;
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
public class CountryService {

    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    /**
     * Récupère tous les pays actifs (avec cache)
     */
    @Cacheable(value = "countries", key = "'all-active'")
    @Transactional(readOnly = true)
    public List<CountryDTO> getAllActiveCountries() {
        logger.debug("Fetching all active countries from database");
        List<Country> countries = countryRepository.findAllActiveOrdered();
        return countries.stream()
                .map(countryMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un pays par son ID (avec cache)
     */
    @Cacheable(value = "country", key = "#id")
    @Transactional(readOnly = true)
    public CountryDTO getCountryById(Long id) {
        logger.debug("Fetching country with id: {}", id);
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        return countryMapper.toDTO(country, true); // Inclure les villes
    }

    /**
     * Récupère un pays par son code (avec cache)
     */
    @Cacheable(value = "country", key = "'code-' + #code")
    @Transactional(readOnly = true)
    public CountryDTO getCountryByCode(String code) {
        logger.debug("Fetching country with code: {}", code);
        Country country = countryRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Country not found with code: " + code));
        return countryMapper.toDTO(country, true); // Inclure les villes
    }

    /**
     * Crée un nouveau pays (invalide le cache)
     */
    @CacheEvict(value = {"countries", "country"}, allEntries = true)
    public CountryDTO createCountry(CountryDTO countryDTO) {
        logger.info("Creating new country: {}", countryDTO.getName());
        
        // Vérifier si le code existe déjà
        if (countryRepository.existsByCode(countryDTO.getCode())) {
            throw new RuntimeException("Country with code " + countryDTO.getCode() + " already exists");
        }

        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        
        logger.info("Country created successfully with id: {}", country.getId());
        return countryMapper.toDTO(country);
    }

    /**
     * Met à jour un pays (invalide le cache)
     */
    @CacheEvict(value = {"countries", "country"}, allEntries = true)
    public CountryDTO updateCountry(Long id, CountryDTO countryDTO) {
        logger.info("Updating country with id: {}", id);
        
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));

        // Vérifier si le code change et s'il existe déjà
        if (!country.getCode().equals(countryDTO.getCode()) && 
            countryRepository.existsByCode(countryDTO.getCode())) {
            throw new RuntimeException("Country with code " + countryDTO.getCode() + " already exists");
        }

        // Mettre à jour les champs
        country.setCode(countryDTO.getCode());
        country.setName(countryDTO.getName());
        country.setCode3(countryDTO.getCode3());
        country.setPhoneCode(countryDTO.getPhoneCode());
        country.setCurrency(countryDTO.getCurrency());
        country.setCurrencySymbol(countryDTO.getCurrencySymbol());
        country.setTimezone(countryDTO.getTimezone());
        country.setFlagEmoji(countryDTO.getFlagEmoji());
        country.setDescription(countryDTO.getDescription());
        country.setLatitude(countryDTO.getLatitude());
        country.setLongitude(countryDTO.getLongitude());
        country.setImportantData(countryDTO.getImportantData());
        country.setActive(countryDTO.getActive());
        country.setDisplayOrder(countryDTO.getDisplayOrder());

        country = countryRepository.save(country);
        
        logger.info("Country updated successfully with id: {}", country.getId());
        return countryMapper.toDTO(country);
    }

    /**
     * Supprime un pays (invalide le cache)
     */
    @CacheEvict(value = {"countries", "country", "cities", "city", "citiesByCountry"}, allEntries = true)
    public void deleteCountry(Long id) {
        logger.info("Deleting country with id: {}", id);
        
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));

        // Vérifier s'il y a des villes associées
        if (!country.getCities().isEmpty()) {
            throw new RuntimeException("Cannot delete country with id " + id + " because it has associated cities");
        }

        countryRepository.delete(country);
        logger.info("Country deleted successfully with id: {}", id);
    }
}

