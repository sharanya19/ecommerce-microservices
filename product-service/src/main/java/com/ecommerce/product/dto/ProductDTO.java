package com.ecommerce.product.dto;

import com.ecommerce.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private String imageUrl;
    private String sku;
    private Product.ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        dto.setSku(product.getSku());
        dto.setStatus(product.getStatus());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}

