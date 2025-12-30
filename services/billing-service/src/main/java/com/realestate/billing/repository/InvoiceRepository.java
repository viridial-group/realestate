package com.realestate.billing.repository;

import com.realestate.billing.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByOrganizationId(Long organizationId);

    Page<Invoice> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId, Pageable pageable);

    List<Invoice> findByStatus(String status);

    @Query("SELECT i FROM Invoice i WHERE i.organizationId = :organizationId AND i.status = :status ORDER BY i.createdAt DESC")
    Page<Invoice> findByOrganizationIdAndStatus(
            @Param("organizationId") Long organizationId,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT i FROM Invoice i WHERE i.dueDate <= :date AND i.status = 'PENDING'")
    List<Invoice> findOverdueInvoices(@Param("date") LocalDateTime date);

    @Query("SELECT i FROM Invoice i WHERE i.subscription.id = :subscriptionId ORDER BY i.createdAt DESC")
    List<Invoice> findBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
}

