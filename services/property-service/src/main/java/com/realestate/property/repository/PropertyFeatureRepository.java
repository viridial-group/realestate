package com.realestate.property.repository;

import com.realestate.property.entity.PropertyFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyFeatureRepository extends JpaRepository<PropertyFeature, Long> {

    List<PropertyFeature> findByPropertyId(Long propertyId);

    Optional<PropertyFeature> findByPropertyIdAndKey(Long propertyId, String key);

    @Query("SELECT pf FROM PropertyFeature pf WHERE pf.property.id = :propertyId AND pf.active = true")
    List<PropertyFeature> findActiveByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT pf FROM PropertyFeature pf WHERE pf.property.id = :propertyId AND pf.key = :key AND pf.active = true")
    Optional<PropertyFeature> findActiveByPropertyIdAndKey(
            @Param("propertyId") Long propertyId,
            @Param("key") String key
    );
}

