package com.ecommerce.payment.dto;

import com.ecommerce.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
}

