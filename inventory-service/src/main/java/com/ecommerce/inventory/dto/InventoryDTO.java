package com.ecommerce.inventory.dto;

import com.ecommerce.inventory.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private LocalDateTime lastUpdated;
    
    public static InventoryDTO fromEntity(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
        dto.setQuantity(inventory.getQuantity());
        dto.setReservedQuantity(inventory.getReservedQuantity());
        dto.setAvailableQuantity(inventory.getAvailableQuantity());
        dto.setMinStockLevel(inventory.getMinStockLevel());
        dto.setMaxStockLevel(inventory.getMaxStockLevel());
        dto.setLastUpdated(inventory.getLastUpdated());
        return dto;
    }
}

