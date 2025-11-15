package com.ecommerce.order.dto;

import com.ecommerce.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    
    public static OrderDTO fromEntity(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setOrderItems(order.getOrderItems().stream()
            .map(OrderItemDTO::fromEntity)
            .collect(Collectors.toList()));
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}

