package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.OrderStatus;
import com.factory.factory_erp.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder extends BaseEntity {
    
    @Column(name = "sale_id", unique = true, nullable = false, length = 50)
    private String saleId;
    
    @Column(name = "order_number", unique = true, nullable = false, length = 100)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.pending;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.unpaid;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalesOrderItem> items = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        if (saleId == null) {
            saleId = "SO-" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
        if (orderNumber == null) {
            orderNumber = "2026-03-" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
    
    public void addItem(SalesOrderItem item) {
        items.add(item);
        item.setSalesOrder(this);
    }
    
    public void removeItem(SalesOrderItem item) {
        items.remove(item);
        item.setSalesOrder(null);
    }
    
    public void calculateTotals() {
        this.subtotal = items.stream()
            .map(SalesOrderItem::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.total = subtotal.add(tax).subtract(discount);
    }
}
