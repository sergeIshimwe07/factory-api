package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.SupplierOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplier_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierOrder extends BaseEntity {
    
    @Column(name = "supplier_order_id", unique = true, nullable = false, length = 50)
    private String supplierOrderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SupplierOrderStatus status = SupplierOrderStatus.draft;
    
    @Column(name = "proof_of_payment", length = 255)
    private String proofOfPayment;
    
    @Column(length = 255)
    private String invoice;
    
    @OneToMany(mappedBy = "supplierOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SupplierOrderItem> items = new ArrayList<>();
    
    @Column(name = "subtotal", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(name = "tax", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(name = "total", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (supplierOrderId == null) {
            supplierOrderId = "po_" + System.currentTimeMillis();
        }
        calculateTotals();
    }
    
    @PreUpdate
    public void preUpdate() {
        calculateTotals();
    }
    
    public void addItem(SupplierOrderItem item) {
        items.add(item);
        item.setSupplierOrder(this);
    }
    
    public void removeItem(SupplierOrderItem item) {
        items.remove(item);
        item.setSupplierOrder(null);
    }
    
    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(item -> {
                    // Calculate total from quantity and unitPrice if not already set
                    if (item.getTotal() != null) {
                        return item.getTotal();
                    }
                    return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate 10% tax on subtotal
        this.tax = this.subtotal.multiply(BigDecimal.valueOf(0.10));
        this.total = this.subtotal.add(this.tax);
    }
}
