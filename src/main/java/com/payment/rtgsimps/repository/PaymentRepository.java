
package com.payment.rtgsimps.repository;

import com.payment.rtgsimps.domain.Payment;
import com.payment.rtgsimps.domain.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByIdAndCustomerId(
            UUID id,
            String customerId
    );

    Optional<Payment> findByCustomerIdAndIdempotencyKey(
            String customerId,
            String key
    );

    Optional<Payment> findByCustomerIdAndClientReference(
            String customerId,
            String reference
    );

    List<Payment> findTop50ByStatusOrderByCreatedAtAsc(
            PaymentStatus status
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
        update Payment p
           set p.status = :next,
               p.updatedAt = :updatedAt,
               p.version = p.version + 1
         where p.id = :id
           and p.status = :expected
        """)
    int claimForProcessing(
            @Param("id") UUID id,
            @Param("expected") PaymentStatus expected,
            @Param("next") PaymentStatus next,
            @Param("updatedAt") Instant updatedAt
    );
}
