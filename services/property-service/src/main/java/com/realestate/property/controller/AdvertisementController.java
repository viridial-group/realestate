package com.realestate.property.controller;

import com.realestate.property.dto.AdvertisementAnalyticsDTO;
import com.realestate.property.dto.AdvertisementCreateDTO;
import com.realestate.property.dto.AdvertisementDTO;
import com.realestate.property.dto.AdvertisementStatsDTO;
import com.realestate.property.dto.AdvertisementUpdateDTO;
import com.realestate.property.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
@Tag(name = "Advertisements", description = "Advertisement management API for creating and managing advertising campaigns")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PostMapping
    @Operation(summary = "Create advertisement", description = "Creates a new advertisement")
    public ResponseEntity<AdvertisementDTO> createAdvertisement(@Valid @RequestBody AdvertisementCreateDTO dto) {
        AdvertisementDTO created = advertisementService.createAdvertisement(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get advertisement by ID", description = "Returns advertisement information")
    public ResponseEntity<AdvertisementDTO> getAdvertisementById(@PathVariable Long id) {
        AdvertisementDTO advertisement = advertisementService.getAdvertisementById(id);
        return ResponseEntity.ok(advertisement);
    }

    @GetMapping
    @Operation(summary = "List advertisements", description = "Returns a list of advertisements with optional filters")
    public ResponseEntity<List<AdvertisementDTO>> getAllAdvertisements(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) String status) {
        List<AdvertisementDTO> advertisements = advertisementService.getAllAdvertisements(organizationId, status);
        return ResponseEntity.ok(advertisements);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update advertisement", description = "Updates an existing advertisement")
    public ResponseEntity<AdvertisementDTO> updateAdvertisement(
            @PathVariable Long id,
            @Valid @RequestBody AdvertisementUpdateDTO dto) {
        AdvertisementDTO updated = advertisementService.updateAdvertisement(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete advertisement", description = "Deletes an advertisement")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update advertisement status", description = "Updates the status of an advertisement")
    public ResponseEntity<AdvertisementDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        AdvertisementDTO updated = advertisementService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get advertisement statistics", description = "Returns statistics for an advertisement")
    public ResponseEntity<AdvertisementStatsDTO> getAdvertisementStats(@PathVariable Long id) {
        AdvertisementStatsDTO stats = advertisementService.getAdvertisementStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get advertisement analytics", description = "Returns comprehensive analytics for all advertisements with charts data")
    public ResponseEntity<AdvertisementAnalyticsDTO> getAnalytics(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AdvertisementAnalyticsDTO analytics = advertisementService.getAnalytics(organizationId, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    // Endpoints publics pour récupérer les annonces actives
    @GetMapping("/public/active")
    @Operation(summary = "Get active advertisements", description = "Returns active advertisements for display (public endpoint)")
    public ResponseEntity<List<AdvertisementDTO>> getActiveAdvertisements(
            @RequestParam(required = false) String adType,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String propertyType) {
        List<AdvertisementDTO> advertisements = advertisementService.getActiveAdvertisements(
                adType, position, city, postalCode, propertyType);
        return ResponseEntity.ok(advertisements);
    }

    @PostMapping("/public/{id}/impression")
    @Operation(summary = "Record advertisement impression", description = "Records an impression for an advertisement (public endpoint)")
    public ResponseEntity<Void> recordImpression(
            @PathVariable Long id,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) String pageType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postalCode,
            HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        advertisementService.recordImpression(id, ipAddress, userAgent, propertyId, pageType, city, postalCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public/{id}/click")
    @Operation(summary = "Record advertisement click", description = "Records a click for an advertisement (public endpoint)")
    public ResponseEntity<Void> recordClick(
            @PathVariable Long id,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postalCode,
            HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        advertisementService.recordClick(id, ipAddress, userAgent, referrer, propertyId, city, postalCode);
        return ResponseEntity.ok().build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}

