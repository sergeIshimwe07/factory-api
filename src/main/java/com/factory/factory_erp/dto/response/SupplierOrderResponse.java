package com.factory.factory_erp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderResponse {
    
    private String id;
    private String supplierId;
    private String supplierName;
    private String supplierEmail;
    private String supplierPhone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    private String notes;
    private String status;
    private List<SupplierOrderItemResponse> items;
    private Integer itemCount;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private String proofOfPayment;
    private String invoice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
