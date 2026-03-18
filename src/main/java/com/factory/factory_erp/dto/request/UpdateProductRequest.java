package com.factory.factory_erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    
    private String name;
    private String category;
    private String unit;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private Integer minimumStock;
    private Integer reorderLevel;
    private BigDecimal weight;
    private String description;
    private String status;
}
