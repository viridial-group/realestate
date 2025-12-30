package com.realestate.billing.mapper;

import com.realestate.billing.dto.InvoiceDTO;
import com.realestate.billing.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        if (invoice.getSubscription() != null) {
            dto.setSubscriptionId(invoice.getSubscription().getId());
        }
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setOrganizationId(invoice.getOrganizationId());
        dto.setAmount(invoice.getAmount());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setCurrency(invoice.getCurrency());
        dto.setStatus(invoice.getStatus());
        dto.setDueDate(invoice.getDueDate());
        dto.setPaidAt(invoice.getPaidAt());
        dto.setBillingPeriodStart(invoice.getBillingPeriodStart());
        dto.setBillingPeriodEnd(invoice.getBillingPeriodEnd());
        return dto;
    }
}

