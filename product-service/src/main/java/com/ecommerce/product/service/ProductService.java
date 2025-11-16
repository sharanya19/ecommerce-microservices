package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.metrics.ProductMetricsRecorder;
import com.ecommerce.product.repository.ProductRepository;
import io.micrometer.observation.annotation.Observed;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductMetricsRecorder metricsRecorder;
    
    public ProductService(ProductRepository productRepository, KafkaTemplate<String, Object> kafkaTemplate,
                          ProductMetricsRecorder metricsRecorder) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.metricsRecorder = metricsRecorder;
    }
    
    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return ProductDTO.fromEntity(product);
    }
    
    @Cacheable(value = "products", key = "'all'")
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    public List<ProductDTO> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "products", allEntries = true)
    @Observed(name = "product.create", contextualName = "product-create")
    public ProductDTO createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        metricsRecorder.recordProductCreated();
        
        // Publish product created event
        kafkaTemplate.send("product-events", "product.created", savedProduct);
        
        return ProductDTO.fromEntity(savedProduct);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public ProductDTO updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());
        product.setStatus(productDetails.getStatus());
        
        Product updatedProduct = productRepository.save(product);
        
        // Publish product updated event
        kafkaTemplate.send("product-events", "product.updated", updatedProduct);
        
        return ProductDTO.fromEntity(updatedProduct);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        
        // Publish product deleted event
        kafkaTemplate.send("product-events", "product.deleted", id);
    }
    
    @CacheEvict(value = "products", key = "#id")
    @Observed(name = "product.stock", contextualName = "product-stock-update")
    public ProductDTO updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setStock(product.getStock() + quantity);
        if (product.getStock() <= 0) {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        }
        
        Product updatedProduct = productRepository.save(product);
        metricsRecorder.recordStockAdjustment(quantity);
        
        // Publish stock updated event
        kafkaTemplate.send("product-events", "product.stock.updated", updatedProduct);
        
        return ProductDTO.fromEntity(updatedProduct);
    }
}

