package com.realestate.billing.controller;

import com.realestate.billing.dto.InvoiceDTO;
import com.realestate.billing.entity.Invoice;
import com.realestate.billing.mapper.InvoiceMapper;
import com.realestate.billing.service.InvoiceService;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.common.client.IdentityServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing/invoices")
@Tag(name = "Invoices", description = "Invoice management API for managing invoices, payments, and billing")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;
    private final com.realestate.billing.service.InvoicePdfService invoicePdfService;
    private final IdentityServiceClient identityServiceClient;

    public InvoiceController(
            InvoiceService invoiceService, 
            InvoiceMapper invoiceMapper, 
            com.realestate.billing.service.InvoicePdfService invoicePdfService,
            IdentityServiceClient identityServiceClient) {
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
        this.invoicePdfService = invoicePdfService;
        this.identityServiceClient = identityServiceClient;
    }

    @PostMapping
    @Operation(summary = "Create invoice", description = "Creates a new invoice for a subscription")
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody Map<String, Object> request) {
        Long subscriptionId = Long.valueOf(request.get("subscriptionId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        BigDecimal taxAmount = request.containsKey("taxAmount") ? 
                new BigDecimal(request.get("taxAmount").toString()) : BigDecimal.ZERO;
        
        Invoice created = invoiceService.createInvoice(subscriptionId, amount, taxAmount);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(invoiceMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID", description = "Returns invoice information for a specific invoice ID")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
        return ResponseEntity.ok(invoiceMapper.toDTO(invoice));
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Get invoice by number", description = "Returns invoice information for a specific invoice number")
    public ResponseEntity<InvoiceDTO> getInvoiceByInvoiceNumber(@PathVariable String invoiceNumber) {
        Invoice invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceNumber));
        return ResponseEntity.ok(invoiceMapper.toDTO(invoice));
    }

    @GetMapping
    @Operation(summary = "List invoices", description = "Returns a paginated list of invoices with optional filters. Automatically filters by user permissions and accessible organizations.")
    public ResponseEntity<Page<InvoiceDTO>> getInvoices(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // Récupérer le contexte de permissions si un token est fourni
            Set<Long> accessibleOrgIds = null;
            boolean isSuperAdmin = false;
            boolean isAdmin = false;
            
            if (authorization != null && authorization.startsWith("Bearer ") && identityServiceClient != null) {
                String token = authorization.substring(7);
                
                try {
                    java.util.Optional<com.realestate.common.client.dto.PermissionContextDTO> permissionContextOpt = 
                            identityServiceClient.getPermissionContext(token).block();
                    
                    if (permissionContextOpt.isPresent()) {
                        com.realestate.common.client.dto.PermissionContextDTO permissionContext = permissionContextOpt.get();
                        isSuperAdmin = permissionContext.isSuperAdmin();
                        isAdmin = permissionContext.isAdmin();
                        accessibleOrgIds = permissionContext.getAccessibleOrganizationIds();
                    }
                } catch (Exception e) {
                    // Continuer sans contexte de permissions
                }
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Invoice> invoices;
            
            // Si l'utilisateur n'est pas super admin/admin, filtrer selon ses permissions
            if (!isSuperAdmin && !isAdmin && accessibleOrgIds != null && !accessibleOrgIds.isEmpty()) {
                // Si un organizationId est spécifié, vérifier qu'il est accessible
                if (organizationId != null && !accessibleOrgIds.contains(organizationId)) {
                    // L'utilisateur n'a pas accès à cette organisation
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                
                // Utiliser le filtrage avec permissions
                invoices = invoiceService.getInvoicesWithPermissions(accessibleOrgIds, organizationId, status, pageable);
            } else {
                // Super admin ou admin : voir toutes les factures
                if (organizationId == null) {
                    return ResponseEntity.badRequest().build();
                }
                if (status != null) {
                    invoices = invoiceService.getInvoicesByOrganizationIdAndStatus(organizationId, status, pageable);
                } else {
                    invoices = invoiceService.getInvoicesByOrganizationId(organizationId, pageable);
                }
            }
            
            Page<InvoiceDTO> invoiceDTOs = invoices.map(invoiceMapper::toDTO);
            return ResponseEntity.ok(invoiceDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/subscription/{subscriptionId}")
    @Operation(summary = "Get invoices by subscription", description = "Returns all invoices for a specific subscription")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesBySubscriptionId(@PathVariable Long subscriptionId) {
        List<Invoice> invoices = invoiceService.getInvoicesBySubscriptionId(subscriptionId);
        List<InvoiceDTO> invoiceDTOs = invoices.stream()
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoiceDTOs);
    }

    @PostMapping("/{id}/mark-paid")
    @Operation(summary = "Mark invoice as paid", description = "Marks an invoice as paid")
    public ResponseEntity<InvoiceDTO> markInvoiceAsPaid(@PathVariable Long id) {
        Invoice invoice = invoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.ok(invoiceMapper.toDTO(invoice));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update invoice status", description = "Updates the status of an invoice")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Invoice invoice = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(invoiceMapper.toDTO(invoice));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices", description = "Returns invoices that are overdue")
    public ResponseEntity<List<InvoiceDTO>> getOverdueInvoices(@RequestParam(required = false) LocalDateTime date) {
        LocalDateTime checkDate = date != null ? date : LocalDateTime.now();
        List<Invoice> invoices = invoiceService.getOverdueInvoices(checkDate);
        List<InvoiceDTO> invoiceDTOs = invoices.stream()
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoiceDTOs);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download invoice PDF", description = "Downloads the invoice as a PDF file")
    public ResponseEntity<Resource> downloadInvoicePdf(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
        
        try {
            byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoice);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=invoice-" + invoice.getInvoiceNumber() + ".html")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }

    @PostMapping("/subscription/{subscriptionId}/generate")
    @Operation(summary = "Generate invoice for subscription", description = "Automatically generates an invoice for a subscription")
    public ResponseEntity<InvoiceDTO> generateInvoiceForSubscription(@PathVariable Long subscriptionId) {
        Invoice invoice = invoiceService.generateInvoiceForSubscription(subscriptionId);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(invoiceMapper.toDTO(invoice));
    }
}

