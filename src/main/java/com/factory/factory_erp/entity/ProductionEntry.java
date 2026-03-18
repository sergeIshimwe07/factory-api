package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.ProductionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "production_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionEntry extends BaseEntity {
    
    @Column(name = "production_id", unique = true, nullable = false, length = 50)
    private String productionId;
    
    @Column(name = "batch_number", unique = true, nullable = false, length = 100)
    private String batchNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private BillOfMaterials billOfMaterials;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "quantity_produced", nullable = false)
    private Integer quantityProduced;
    
    @Column(name = "quantity_defective")
    @Builder.Default
    private Integer quantityDefective = 0;
    
    @Column(length = 100)
    private String supervisor;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductionStatus status = ProductionStatus.planned;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    public void prePersist() {
        if (productionId == null) {
            productionId = "prod_entry_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
        if (batchNumber == null) {
            batchNumber = "BATCH-2026-03-" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
    }
}
