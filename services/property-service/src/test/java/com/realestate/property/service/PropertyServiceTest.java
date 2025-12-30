package com.realestate.property.service;

import com.realestate.property.entity.Property;
import com.realestate.property.entity.PropertyAccess;
import com.realestate.property.entity.PropertyFeature;
import com.realestate.property.repository.PropertyAccessRepository;
import com.realestate.property.repository.PropertyFeatureRepository;
import com.realestate.property.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyAccessRepository propertyAccessRepository;

    @Mock
    private PropertyFeatureRepository propertyFeatureRepository;

    @InjectMocks
    private PropertyService propertyService;

    private Property testProperty;

    @BeforeEach
    void setUp() {
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setReference("PROP-001");
        testProperty.setTitle("Beautiful Apartment");
        testProperty.setDescription("A beautiful apartment in the city center");
        testProperty.setType("APARTMENT");
        testProperty.setStatus("PUBLISHED");
        testProperty.setPrice(new BigDecimal("250000.00"));
        testProperty.setCurrency("EUR");
        testProperty.setSurface(new BigDecimal("85.50"));
        testProperty.setRooms(4);
        testProperty.setBedrooms(2);
        testProperty.setBathrooms(1);
        testProperty.setCity("Paris");
        testProperty.setCountry("France");
        testProperty.setOrganizationId(100L);
        testProperty.setActive(true);
    }

    @Test
    void testCreateProperty_Success() {
        // Given
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // When
        Property result = propertyService.createProperty(testProperty);

        // Then
        assertNotNull(result);
        assertEquals(testProperty.getReference(), result.getReference());
        verify(propertyRepository).save(testProperty);
    }

    @Test
    void testGetPropertyById_Success() {
        // Given
        Long id = 1L;
        when(propertyRepository.findById(id)).thenReturn(Optional.of(testProperty));

        // When
        Optional<Property> result = propertyService.getPropertyById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProperty.getId(), result.get().getId());
        verify(propertyRepository).findById(id);
    }

    @Test
    void testGetPropertyByReference_Success() {
        // Given
        String reference = "PROP-001";
        when(propertyRepository.findByReference(reference)).thenReturn(Optional.of(testProperty));

        // When
        Optional<Property> result = propertyService.getPropertyByReference(reference);

        // Then
        assertTrue(result.isPresent());
        assertEquals(reference, result.get().getReference());
        verify(propertyRepository).findByReference(reference);
    }

    @Test
    void testGetPropertiesByOrganizationId_Success() {
        // Given
        Long organizationId = 100L;
        List<Property> properties = Arrays.asList(testProperty);
        when(propertyRepository.findActiveByOrganizationId(organizationId)).thenReturn(properties);

        // When
        List<Property> result = propertyService.getPropertiesByOrganizationId(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(propertyRepository).findActiveByOrganizationId(organizationId);
    }

    @Test
    void testUpdateProperty_Success() {
        // Given
        Long id = 1L;
        Property updateDetails = new Property();
        updateDetails.setTitle("Updated Title");
        updateDetails.setPrice(new BigDecimal("300000.00"));

        when(propertyRepository.findById(id)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // When
        Property result = propertyService.updateProperty(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(new BigDecimal("300000.00"), result.getPrice());
        verify(propertyRepository).findById(id);
        verify(propertyRepository).save(testProperty);
    }

    @Test
    void testUpdateProperty_NotFound() {
        // Given
        Long id = 999L;
        Property updateDetails = new Property();
        when(propertyRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> propertyService.updateProperty(id, updateDetails));
        verify(propertyRepository).findById(id);
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void testDeleteProperty_Success() {
        // Given
        Long id = 1L;
        when(propertyRepository.existsById(id)).thenReturn(true);
        doNothing().when(propertyRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> propertyService.deleteProperty(id));

        // Then
        verify(propertyRepository).existsById(id);
        verify(propertyRepository).deleteById(id);
    }

    @Test
    void testSharePropertyWithOrganization_Success() {
        // Given
        Long propertyId = 1L;
        Long organizationId = 200L;
        Long userId = 10L;
        Boolean canRead = true;
        Boolean canWrite = true;
        Boolean canDelete = false;
        Long grantedBy = 1L;

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyAccessRepository.findByPropertyIdAndUserIdAndOrganizationId(propertyId, userId, organizationId))
                .thenReturn(Optional.empty());
        when(propertyAccessRepository.save(any(PropertyAccess.class))).thenReturn(new PropertyAccess());

        // When
        PropertyAccess result = propertyService.sharePropertyWithOrganization(
                propertyId, organizationId, userId, canRead, canWrite, canDelete, grantedBy);

        // Then
        assertNotNull(result);
        verify(propertyRepository).findById(propertyId);
        verify(propertyAccessRepository).save(any(PropertyAccess.class));
    }

    @Test
    void testAddFeatureToProperty_Success() {
        // Given
        Long propertyId = 1L;
        PropertyFeature feature = new PropertyFeature();
        feature.setKey("has_parking");
        feature.setValue("true");
        feature.setType("BOOLEAN");

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyFeatureRepository.findByPropertyIdAndKey(propertyId, feature.getKey()))
                .thenReturn(Optional.empty());
        when(propertyFeatureRepository.save(any(PropertyFeature.class))).thenReturn(feature);

        // When
        PropertyFeature result = propertyService.addFeatureToProperty(propertyId, feature);

        // Then
        assertNotNull(result);
        assertEquals("has_parking", result.getKey());
        verify(propertyRepository).findById(propertyId);
        verify(propertyFeatureRepository).save(any(PropertyFeature.class));
    }
}

