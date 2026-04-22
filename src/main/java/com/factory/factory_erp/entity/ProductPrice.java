package com.factory.factory_erp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_price", indexes = {
    @Index(name = "idx_product_id_start_date", columnList = "product_id, start_date"),
    @Index(name = "idx_product_id_end_date", columnList = "product_id, end_date"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPrice extends BaseEntity {
    
    /**
     * Decoupled from Product entity to maintain historical price data.
     * When a product price changes, past records and reports retain accurate historical prices.
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "USD";
    
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PriceStatus status = PriceStatus.ACTIVE;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator", nullable = false)
    private User operator;
    
    /**
     * Checks if this price is currently active for the given date
     */
    public boolean isActiveOn(LocalDate date) {
        if (status != PriceStatus.ACTIVE) {
            return false;
        }
        boolean afterStart = !date.isBefore(startDate);
        boolean beforeEnd = endDate == null || !date.isAfter(endDate);
        return afterStart && beforeEnd;
    }
    
    /**
     * Marks this price as expired and sets the end date
     */
    public void expire(LocalDate expiryDate) {
        this.endDate = expiryDate;
        this.status = PriceStatus.EXPIRED;
    }
    
    /**
     * Status enum for price tracking
     */
    public enum PriceStatus {
        ACTIVE,      // Currently active price
        EXPIRED,     // Price validity has ended
        SUPERSEDED,  // Price has been replaced by a newer price
        ARCHIVED     // Price no longer in use but kept for historical records
    }
}
