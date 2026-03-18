package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.CommissionType;
import com.factory.factory_erp.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "commission_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRule extends BaseEntity {
    
    @Column(name = "rule_id", unique = true, nullable = false, length = 50)
    private String ruleId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommissionType type;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.active;
    
    @PrePersist
    public void prePersist() {
        if (ruleId == null) {
            ruleId = "rule_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
    }
}
