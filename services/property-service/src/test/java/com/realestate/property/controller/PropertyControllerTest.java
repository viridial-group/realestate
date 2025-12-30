package com.realestate.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.property.entity.Property;
import com.realestate.property.entity.PropertyFeature;
import com.realestate.property.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PropertyController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Property createTestProperty() {
        Property property = new Property();
        property.setId(1L);
        property.setReference("PROP-001");
        property.setTitle("Beautiful Apartment");
        property.setDescription("A beautiful apartment in the city center");
        property.setType("APARTMENT");
        property.setStatus("PUBLISHED");
        property.setPrice(new BigDecimal("250000.00"));
        property.setCurrency("EUR");
        property.setSurface(new BigDecimal("85.50"));
        property.setRooms(4);
        property.setBedrooms(2);
        property.setBathrooms(1);
        property.setCity("Paris");
        property.setCountry("France");
        property.setOrganizationId(100L);
        property.setActive(true);
        return property;
    }

    @Test
    void testCreateProperty_Success() throws Exception {
        // Given
        Property property = createTestProperty();
        when(propertyService.createProperty(any(Property.class))).thenReturn(property);

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(property)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("PROP-001"))
                .andExpect(jsonPath("$.title").value("Beautiful Apartment"));
    }

    @Test
    void testGetPropertyById_Success() throws Exception {
        // Given
        Long id = 1L;
        Property property = createTestProperty();
        when(propertyService.getPropertyById(id)).thenReturn(Optional.of(property));

        // When & Then
        mockMvc.perform(get("/api/properties/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reference").value("PROP-001"));
    }

    @Test
    void testGetPropertyByReference_Success() throws Exception {
        // Given
        String reference = "PROP-001";
        Property property = createTestProperty();
        when(propertyService.getPropertyByReference(reference)).thenReturn(Optional.of(property));

        // When & Then
        mockMvc.perform(get("/api/properties/reference/{reference}", reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("PROP-001"));
    }

    @Test
    void testGetPropertiesByOrganizationId_Success() throws Exception {
        // Given
        Long organizationId = 100L;
        List<Property> properties = Arrays.asList(createTestProperty());
        when(propertyService.getPropertiesByOrganizationId(organizationId)).thenReturn(properties);

        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("organizationId", organizationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateProperty_Success() throws Exception {
        // Given
        Long id = 1L;
        Property updateDetails = createTestProperty();
        updateDetails.setTitle("Updated Title");
        Property updated = createTestProperty();
        updated.setTitle("Updated Title");

        when(propertyService.updateProperty(eq(id), any(Property.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/properties/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testDeleteProperty_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(propertyService).deleteProperty(id);

        // When & Then
        mockMvc.perform(delete("/api/properties/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAddFeatureToProperty_Success() throws Exception {
        // Given
        Long id = 1L;
        PropertyFeature feature = new PropertyFeature();
        feature.setKey("has_parking");
        feature.setValue("true");
        feature.setType("BOOLEAN");

        when(propertyService.addFeatureToProperty(eq(id), any(PropertyFeature.class))).thenReturn(feature);

        // When & Then
        mockMvc.perform(post("/api/properties/{id}/features", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feature)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("has_parking"));
    }
}

