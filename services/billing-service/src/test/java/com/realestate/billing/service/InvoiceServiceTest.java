package com.realestate.billing.service;

import com.realestate.billing.entity.Invoice;
import com.realestate.billing.entity.Plan;
import com.realestate.billing.entity.Subscription;
import com.realestate.billing.repository.InvoiceRepository;
import com.realestate.billing.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Plan testPlan;
    private Subscription testSubscription;
    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        testPlan = new Plan();
        testPlan.setId(1L);
        testPlan.setCurrency("EUR");

        testSubscription = new Subscription();
        testSubscription.setId(1L);
        testSubscription.setPlan(testPlan);
        testSubscription.setOrganizationId(100L);
        testSubscription.setStartDate(LocalDateTime.now());
        testSubscription.setEndDate(LocalDateTime.now().plusMonths(1));

        testInvoice = new Invoice();
        testInvoice.setId(1L);
        testInvoice.setSubscription(testSubscription);
        testInvoice.setInvoiceNumber("INV-12345678");
        testInvoice.setAmount(new BigDecimal("29.99"));
        testInvoice.setTotalAmount(new BigDecimal("29.99"));
        testInvoice.setStatus("PENDING");
    }

    @Test
    void testCreateInvoice_Success() {
        // Given
        Long subscriptionId = 1L;
        BigDecimal amount = new BigDecimal("29.99");
        BigDecimal taxAmount = new BigDecimal("5.99");
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(testSubscription));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // When
        Invoice result = invoiceService.createInvoice(subscriptionId, amount, taxAmount);

        // Then
        assertNotNull(result);
        verify(subscriptionRepository).findById(subscriptionId);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testMarkInvoiceAsPaid_Success() {
        // Given
        Long id = 1L;
        when(invoiceRepository.findById(id)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // When
        Invoice result = invoiceService.markInvoiceAsPaid(id);

        // Then
        assertNotNull(result);
        assertEquals("PAID", result.getStatus());
        assertNotNull(result.getPaidAt());
        verify(invoiceRepository).findById(id);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testGetInvoiceById_Success() {
        // Given
        Long id = 1L;
        when(invoiceRepository.findById(id)).thenReturn(Optional.of(testInvoice));

        // When
        Optional<Invoice> result = invoiceService.getInvoiceById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testInvoice.getId(), result.get().getId());
        verify(invoiceRepository).findById(id);
    }
}

