package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryDTO;
import com.ecommerce.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }
    
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }
    
    @PostMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> createInventory(
            @PathVariable Long productId,
            @RequestParam Integer initialQuantity) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(inventoryService.createInventory(productId, initialQuantity));
    }
    
    @PatchMapping("/product/{productId}/quantity")
    public ResponseEntity<InventoryDTO> updateQuantity(
            @PathVariable Long productId,
            @RequestParam Integer quantityChange) {
        return ResponseEntity.ok(inventoryService.updateQuantity(productId, quantityChange));
    }
    
    @PatchMapping("/product/{productId}/reserve")
    public ResponseEntity<InventoryDTO> reserveQuantity(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.reserveQuantity(productId, quantity));
    }
    
    @PatchMapping("/product/{productId}/release")
    public ResponseEntity<InventoryDTO> releaseReservation(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.releaseReservation(productId, quantity));
    }
    
    @PatchMapping("/product/{productId}/confirm")
    public ResponseEntity<InventoryDTO> confirmReservation(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.confirmReservation(productId, quantity));
    }
}

