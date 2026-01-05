package com.realestate.billing.service;

import com.realestate.billing.entity.Invoice;
import com.realestate.billing.entity.Subscription;
import com.realestate.billing.repository.InvoiceRepository;
import com.realestate.billing.repository.SubscriptionRepository;
import com.realestate.billing.specification.InvoiceSpecification;
import com.realestate.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, SubscriptionRepository subscriptionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Invoice createInvoice(Long subscriptionId, BigDecimal amount, BigDecimal taxAmount) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", subscriptionId));

        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        BigDecimal totalAmount = amount.add(taxAmount != null ? taxAmount : BigDecimal.ZERO);

        Invoice invoice = new Invoice(subscription, invoiceNumber, totalAmount);
        invoice.setAmount(amount);
        invoice.setTaxAmount(taxAmount);
        invoice.setCurrency(subscription.getPlan().getCurrency());
        invoice.setStatus("PENDING");
        invoice.setDueDate(LocalDateTime.now().plusDays(30)); // 30 jours pour payer
        invoice.setBillingPeriodStart(subscription.getStartDate());
        invoice.setBillingPeriodEnd(subscription.getEndDate());

        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    @Transactional(readOnly = true)
    public Page<Invoice> getInvoicesByOrganizationId(Long organizationId, Pageable pageable) {
        return invoiceRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Invoice> getInvoicesByOrganizationIdAndStatus(Long organizationId, String status, Pageable pageable) {
        return invoiceRepository.findByOrganizationIdAndStatus(organizationId, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesBySubscriptionId(Long subscriptionId) {
        return invoiceRepository.findBySubscriptionId(subscriptionId);
    }

    @Transactional
    public Invoice markInvoiceAsPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));

        invoice.setStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice updateInvoiceStatus(Long id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));

        invoice.setStatus(status);
        if ("PAID".equals(status)) {
            invoice.setPaidAt(LocalDateTime.now());
        }

        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public List<Invoice> getOverdueInvoices(LocalDateTime date) {
        return invoiceRepository.findOverdueInvoices(date);
    }

    /**
     * Génère automatiquement une facture pour un abonnement
     * Utilise le prix du plan comme montant de base
     */
    @Transactional
    public Invoice generateInvoiceForSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", subscriptionId));

        // Calculer le montant depuis le plan
        BigDecimal amount = subscription.getPlan().getPrice();
        
        // Calculer la TVA (20% par défaut en France)
        BigDecimal taxRate = new BigDecimal("0.20");
        BigDecimal taxAmount = amount.multiply(taxRate);
        
        // Créer la facture
        return createInvoice(subscriptionId, amount, taxAmount);
    }
}

