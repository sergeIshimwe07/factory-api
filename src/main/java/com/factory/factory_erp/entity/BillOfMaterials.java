package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bill_of_materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillOfMaterials extends BaseEntity {
    
    @Column(name = "bom_id", unique = true, nullable = false, length = 50)
    private String bomId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finished_product_id", nullable = false)
    private Product finishedProduct;
    
    @OneToMany(mappedBy = "billOfMaterials", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BomItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.active;
    
    @PrePersist
    public void prePersist() {
        if (bomId == null) {
            bomId = "bom_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
    }
    
    public void addItem(BomItem item) {
        items.add(item);
        item.setBillOfMaterials(this);
    }
    
    public void removeItem(BomItem item) {
        items.remove(item);
        item.setBillOfMaterials(null);
    }
}
