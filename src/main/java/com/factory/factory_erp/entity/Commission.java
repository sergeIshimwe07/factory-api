package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.CommissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "commissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commission extends BaseEntity {
    
    @Column(name = "commission_id", unique = true, nullable = false, length = 50)
    private String commissionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;
    
    @Column(nullable = false, length = 7)
    private String period;
    
    @Column(name = "sale_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal saleAmount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal commission;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CommissionStatus status = CommissionStatus.unpaid;
    
    @Column(length = 100)
    private String reference;
    
    @Column(name = "paid_date")
    private LocalDate paidDate;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    public void prePersist() {
        if (commissionId == null) {
            commissionId = "comm_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
    }
}
