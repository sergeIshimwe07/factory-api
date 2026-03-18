package com.factory.factory_erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    
    private String id;
    private String name;
    private String type;
    private String email;
    private String phone;
    private String address;
    private String city;
    private BigDecimal creditLimit;
    private BigDecimal outstandingCredit;
    private BigDecimal totalSales;
    private Integer totalOrders;
    private LocalDateTime lastOrderDate;
    private Boolean isBlocked;
    private String status;
    private LocalDateTime createdDate;
}
