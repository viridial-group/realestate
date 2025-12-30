package com.realestate.billing.repository;

import com.realestate.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByStatus(String status);

    @Query("SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId ORDER BY p.createdAt DESC")
    List<Payment> findByInvoiceIdOrderByCreatedAtDesc(@Param("invoiceId") Long invoiceId);
}

