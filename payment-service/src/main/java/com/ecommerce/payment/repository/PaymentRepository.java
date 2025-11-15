package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByUserId(Long userId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}

