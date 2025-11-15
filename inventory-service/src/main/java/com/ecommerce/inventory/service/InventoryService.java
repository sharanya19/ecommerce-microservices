package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryDTO;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public InventoryService(InventoryRepository inventoryRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Cacheable(value = "inventory", key = "#productId")
    public InventoryDTO getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        return InventoryDTO.fromEntity(inventory);
    }
    
    public List<InventoryDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
            .map(InventoryDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "inventory", key = "#productId")
    public InventoryDTO createInventory(Long productId, Integer initialQuantity) {
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            throw new RuntimeException("Inventory already exists for product: " + productId);
        }
        
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(initialQuantity);
        inventory.setReservedQuantity(0);
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        // Publish inventory created event
        kafkaTemplate.send("inventory-events", "inventory.created", savedInventory);
        
        return InventoryDTO.fromEntity(savedInventory);
    }
    
    @CacheEvict(value = "inventory", key = "#productId")
    public InventoryDTO updateQuantity(Long productId, Integer quantityChange) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        
        inventory.setQuantity(inventory.getQuantity() + quantityChange);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        
        // Publish inventory updated event
        kafkaTemplate.send("inventory-events", "inventory.updated", updatedInventory);
        
        return InventoryDTO.fromEntity(updatedInventory);
    }
    
    @CacheEvict(value = "inventory", key = "#productId")
    public InventoryDTO reserveQuantity(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        
        if (!inventory.isAvailable(quantity)) {
            throw new RuntimeException("Insufficient inventory for product: " + productId);
        }
        
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        
        // Publish inventory reserved event
        kafkaTemplate.send("inventory-events", "inventory.reserved", updatedInventory);
        
        return InventoryDTO.fromEntity(updatedInventory);
    }
    
    @CacheEvict(value = "inventory", key = "#productId")
    public InventoryDTO releaseReservation(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        
        inventory.setReservedQuantity(Math.max(0, inventory.getReservedQuantity() - quantity));
        Inventory updatedInventory = inventoryRepository.save(inventory);
        
        return InventoryDTO.fromEntity(updatedInventory);
    }
    
    @CacheEvict(value = "inventory", key = "#productId")
    public InventoryDTO confirmReservation(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setReservedQuantity(Math.max(0, inventory.getReservedQuantity() - quantity));
        Inventory updatedInventory = inventoryRepository.save(inventory);
        
        // Publish inventory confirmed event
        kafkaTemplate.send("inventory-events", "inventory.confirmed", updatedInventory);
        
        return InventoryDTO.fromEntity(updatedInventory);
    }
    
    @KafkaListener(topics = "order-events", groupId = "inventory-service-group")
    public void handleOrderEvent(String event) {
        // Handle order events
        System.out.println("Received order event: " + event);
    }
}

