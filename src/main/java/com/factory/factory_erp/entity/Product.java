package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.ProductStatus;
import com.factory.factory_erp.entity.enums.ProductUnit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    
    @Column(name = "product_id", unique = true, nullable = false, length = 50)
    private String productId;
    
    @Column(unique = true, nullable = false, length = 100)
    private String sku;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 100)
    private String category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductUnit unit = ProductUnit.piece;
    
    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "cost_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal costPrice;
    
    @Column(name = "current_stock")
    @Builder.Default
    private Integer currentStock = 0;
    
    @Column(name = "minimum_stock")
    @Builder.Default
    private Integer minimumStock = 0;
    
    @Column(name = "reorder_level")
    @Builder.Default
    private Integer reorderLevel = 0;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal weight;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.active;
    
    @PrePersist
    public void prePersist() {
        if (productId == null) {
            productId = "prod_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
        if (sku == null) {
            sku = "SKU-" + System.currentTimeMillis();
        }
    }
}
