package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement extends BaseEntity {
    
    @Column(name = "movement_id", unique = true, nullable = false, length = 50)
    private String movementId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(length = 100)
    private String reference;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @PrePersist
    public void prePersist() {
        if (movementId == null) {
            movementId = "mov_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
    }
}
