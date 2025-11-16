package com.ecommerce.order.service;

import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Cacheable(value = "orders", key = "#id")
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
        return OrderDTO.fromEntity(order);
    }
    
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
            .map(OrderDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(OrderDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "orders", allEntries = true)
    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            // In real scenario, fetch product details from Product Service
            item.setProductName("Product " + itemRequest.getProductId());
            item.setPrice(BigDecimal.valueOf(100)); // Mock price
            item.calculateSubtotal();
            order.getOrderItems().add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        // Publish order created event
        kafkaTemplate.send("order-events", "order.created", savedOrder);
        
        // Request inventory check
        kafkaTemplate.send("inventory-events", "inventory.check", request);
        
        return OrderDTO.fromEntity(savedOrder);
    }
    
    @CacheEvict(value = "orders", key = "#id")
    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        // Publish order status updated event
        kafkaTemplate.send("order-events", "order.status.updated", updatedOrder);
        
        return OrderDTO.fromEntity(updatedOrder);
    }
    
    @CacheEvict(value = "orders", key = "#id")
    public OrderDTO updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
        order.setPaymentStatus(paymentStatus);
        
        if (paymentStatus == Order.PaymentStatus.PAID) {
            order.setStatus(Order.OrderStatus.CONFIRMED);
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        // Publish payment status updated event
        kafkaTemplate.send("order-events", "order.payment.updated", updatedOrder);
        
        return OrderDTO.fromEntity(updatedOrder);
    }
    
    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    public void handlePaymentEvent(String event) {
        // Handle payment events from payment service
        System.out.println("Received payment event: " + event);
    }
}

