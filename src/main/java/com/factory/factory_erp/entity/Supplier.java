package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {
    
    @Column(name = "supplier_id", unique = true, nullable = false, length = 50)
    private String supplierId;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "contact_person", length = 100)
    private String contactPerson;
    
    @Column(name = "total_purchases", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalPurchases = BigDecimal.ZERO;
    
    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.active;
    
    @PrePersist
    public void prePersist() {
        if (supplierId == null) {
            supplierId = "sup_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
    }
}
