package com.factory.factory_erp.entity;

import com.factory.factory_erp.entity.enums.CustomerType;
import com.factory.factory_erp.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {
    
    @Column(name = "customer_id", unique = true, nullable = false, length = 50)
    private String customerId;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(name = "credit_limit", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal creditLimit = BigDecimal.ZERO;
    
    @Column(name = "outstanding_credit", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal outstandingCredit = BigDecimal.ZERO;
    
    @Column(name = "total_sales", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalSales = BigDecimal.ZERO;
    
    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;
    
    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;
    
    @Column(name = "is_blocked")
    @Builder.Default
    private Boolean isBlocked = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.active;
    
    @PrePersist
    public void prePersist() {
        if (customerId == null) {
            customerId = "cust_" + String.format("%03d", System.currentTimeMillis() % 1000);
        }
    }
}
