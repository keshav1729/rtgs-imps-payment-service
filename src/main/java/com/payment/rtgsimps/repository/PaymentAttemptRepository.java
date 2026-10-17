package com.payment.rtgsimps.repository;
import com.payment.rtgsimps.domain.PaymentAttempt;import org.springframework.data.jpa.repository.JpaRepository;import java.util.UUID;
public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt,UUID>{}
