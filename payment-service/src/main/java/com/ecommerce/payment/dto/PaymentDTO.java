package com.ecommerce.payment.dto;

import com.ecommerce.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
    private Payment.PaymentStatus status;
    private String transactionId;
    private String paymentGatewayResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PaymentDTO fromEntity(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentGatewayResponse(payment.getPaymentGatewayResponse());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}

