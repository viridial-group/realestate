package com.realestate.resource.controller;

import com.realestate.resource.entity.Domain;
import com.realestate.resource.service.DomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources/domains")
@Tag(name = "Domains", description = "Domain management API for organizing resources by domain")
public class DomainController {

    private final DomainService domainService;

    public DomainController(DomainService domainService) {
        this.domainService = domainService;
    }

    @PostMapping
    @Operation(summary = "Create domain", description = "Creates a new domain for organizing resources")
    public ResponseEntity<Domain> createDomain(@Valid @RequestBody Domain domain) {
        Domain created = domainService.createDomain(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get domain by ID", description = "Returns domain information for a specific domain ID")
    public ResponseEntity<Domain> getDomainById(@PathVariable Long id) {
        return domainService.getDomainById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List all domains", description = "Returns a list of all domains")
    public ResponseEntity<List<Domain>> getAllDomains(@RequestParam(required = false) Boolean active) {
        List<Domain> domains = active != null && active
                ? domainService.getActiveDomains()
                : domainService.getAllDomains();
        return ResponseEntity.ok(domains);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update domain", description = "Updates domain information for a specific domain ID")
    public ResponseEntity<Domain> updateDomain(
            @PathVariable Long id,
            @Valid @RequestBody Domain domainDetails) {
        try {
            Domain updated = domainService.updateDomain(id, domainDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete domain", description = "Deletes a domain from the database by ID")
    public ResponseEntity<Void> deleteDomain(@PathVariable Long id) {
        try {
            domainService.deleteDomain(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

