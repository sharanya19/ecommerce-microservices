package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.CreatePaymentRequest;
import com.ecommerce.payment.dto.PaymentDTO;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.metrics.PaymentMetricsRecorder;
import com.ecommerce.payment.repository.PaymentRepository;
import io.micrometer.observation.annotation.Observed;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();
    private final PaymentMetricsRecorder metricsRecorder;
    
    public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, Object> kafkaTemplate,
                          PaymentMetricsRecorder metricsRecorder) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.metricsRecorder = metricsRecorder;
    }
    
    @Cacheable(value = "payments", key = "#id")
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return PaymentDTO.fromEntity(payment);
    }
    
    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Payment not found with transaction id: " + transactionId));
        return PaymentDTO.fromEntity(payment);
    }
    
    public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
            .map(PaymentDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
            .map(PaymentDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
            .map(PaymentDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "payments", allEntries = true)
    @Observed(name = "payment.process", contextualName = "payment-process")
    public PaymentDTO processPayment(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing();
        
        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaymentGatewayResponse("Payment successful");
            metricsRecorder.recordPaymentCompleted();
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("Payment failed - insufficient funds");
            metricsRecorder.recordPaymentFailed();
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Publish payment processed event
        kafkaTemplate.send("payment-events", "payment.processed", updatedPayment);
        
        return PaymentDTO.fromEntity(updatedPayment);
    }
    
    @CacheEvict(value = "payments", key = "#id")
    @Observed(name = "payment.refund", contextualName = "payment-refund")
    public PaymentDTO refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setPaymentGatewayResponse("Refund processed successfully");
        Payment updatedPayment = paymentRepository.save(payment);
        metricsRecorder.recordRefund();
        
        // Publish payment refunded event
        kafkaTemplate.send("payment-events", "payment.refunded", updatedPayment);
        
        return PaymentDTO.fromEntity(updatedPayment);
    }
    
    private boolean simulatePaymentProcessing() {
        // Simulate payment gateway processing (90% success rate)
        return random.nextInt(10) < 9;
    }
    
    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void handleOrderEvent(String event) {
        // Handle order events
        System.out.println("Received order event: " + event);
    }
}

