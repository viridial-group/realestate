package com.realestate.billing.mapper;

import com.realestate.billing.dto.PaymentDTO;
import com.realestate.billing.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        if (payment.getInvoice() != null) {
            dto.setInvoiceId(payment.getInvoice().getId());
        }
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentGatewayResponse(payment.getPaymentGatewayResponse());
        dto.setPaidAt(payment.getPaidAt());
        dto.setErrorMessage(payment.getErrorMessage());
        return dto;
    }
}

