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
public class PaymentResponse {
    
    private String id;
    private String saleId;
    private String saleInvoice;
    private String customerId;
    private String customerName;
    private BigDecimal amount;
    private String method;
    private String reference;
    private String notes;
    private LocalDateTime createdAt;
    private String createdBy;
}
