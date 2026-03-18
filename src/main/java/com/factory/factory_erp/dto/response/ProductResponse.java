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
public class ProductResponse {
    
    private String id;
    private String sku;
    private String name;
    private String category;
    private String unit;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private Integer currentStock;
    private Integer minimumStock;
    private Integer reorderLevel;
    private BigDecimal weight;
    private String description;
    private String status;
}
