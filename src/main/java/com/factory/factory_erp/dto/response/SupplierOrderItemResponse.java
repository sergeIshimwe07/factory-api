package com.factory.factory_erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderItemResponse {
    
    private Long id;
    private String rawMaterialId;
    private String rawMaterialName;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal total;
}
