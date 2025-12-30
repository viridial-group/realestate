package com.realestate.billing.controller;

import com.realestate.billing.dto.InvoiceDTO;
import com.realestate.billing.entity.Invoice;
import com.realestate.billing.mapper.InvoiceMapper;
import com.realestate.billing.service.InvoiceService;
import com.realestate.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing/invoices")
@Tag(name = "Invoices", description = "Invoice management API for managing invoices, payments, and billing")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    public InvoiceController(InvoiceService invoiceService, InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
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
    @Operation(summary = "List invoices", description = "Returns a paginated list of invoices with optional filters")
    public ResponseEntity<Page<InvoiceDTO>> getInvoices(
            @RequestParam Long organizationId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoices;
        
        if (status != null) {
            invoices = invoiceService.getInvoicesByOrganizationIdAndStatus(organizationId, status, pageable);
        } else {
            invoices = invoiceService.getInvoicesByOrganizationId(organizationId, pageable);
        }
        
        Page<InvoiceDTO> invoiceDTOs = invoices.map(invoiceMapper::toDTO);
        return ResponseEntity.ok(invoiceDTOs);
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
}

