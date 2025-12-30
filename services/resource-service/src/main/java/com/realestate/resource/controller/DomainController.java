package com.realestate.resource.controller;

import com.realestate.resource.dto.DomainDTO;
import com.realestate.resource.entity.Domain;
import com.realestate.resource.mapper.DomainMapper;
import com.realestate.resource.service.DomainService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources/domains")
@Tag(name = "Domains", description = "Domain management API for organizing resources by domain")
public class DomainController {

    private final DomainService domainService;
    private final DomainMapper domainMapper;

    public DomainController(DomainService domainService, DomainMapper domainMapper) {
        this.domainService = domainService;
        this.domainMapper = domainMapper;
    }

    @PostMapping
    @Operation(summary = "Create domain", description = "Creates a new domain for organizing resources")
    public ResponseEntity<DomainDTO> createDomain(@Valid @RequestBody DomainDTO domainDTO) {
        Domain domain = domainMapper.toEntity(domainDTO);
        Domain created = domainService.createDomain(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(domainMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get domain by ID", description = "Returns domain information for a specific domain ID")
    public ResponseEntity<DomainDTO> getDomainById(@PathVariable Long id) {
        Domain domain = domainService.getDomainById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain", id));
        return ResponseEntity.ok(domainMapper.toDTO(domain));
    }

    @GetMapping
    @Operation(summary = "List all domains", description = "Returns a list of all domains")
    public ResponseEntity<List<DomainDTO>> getAllDomains(@RequestParam(required = false) Boolean active) {
        List<Domain> domains = active != null && active
                ? domainService.getActiveDomains()
                : domainService.getAllDomains();
        List<DomainDTO> domainDTOs = domains.stream()
                .map(domainMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(domainDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update domain", description = "Updates domain information for a specific domain ID")
    public ResponseEntity<DomainDTO> updateDomain(
            @PathVariable Long id,
            @Valid @RequestBody DomainDTO domainDTO) {
        Domain domain = domainMapper.toEntity(domainDTO);
        Domain updated = domainService.updateDomain(id, domain);
        return ResponseEntity.ok(domainMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete domain", description = "Deletes a domain from the database by ID")
    public ResponseEntity<Void> deleteDomain(@PathVariable Long id) {
        domainService.deleteDomain(id);
        return ResponseEntity.noContent().build();
    }
}
