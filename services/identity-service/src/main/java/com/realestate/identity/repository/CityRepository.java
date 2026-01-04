package com.realestate.identity.repository;

import com.realestate.identity.entity.City;
import com.realestate.identity.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByCountryAndActiveTrueOrderByDisplayOrderAscNameAsc(Country country);

    List<City> findByCountryCodeAndActiveTrueOrderByDisplayOrderAscNameAsc(String countryCode);

    @Query("SELECT c FROM City c WHERE c.country.id = :countryId AND c.active = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<City> findActiveByCountryIdOrdered(@Param("countryId") Long countryId);

    @Query("SELECT c FROM City c WHERE c.active = true ORDER BY c.country.displayOrder ASC, c.country.name ASC, c.displayOrder ASC, c.name ASC")
    List<City> findAllActiveOrdered();

    Optional<City> findByNameAndCountryCode(String name, String countryCode);

    boolean existsByNameAndCountryCode(String name, String countryCode);
}

