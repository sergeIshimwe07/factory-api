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
public class BomItemResponse {
    
    private String id;
    private String rawMaterialId;
    private String rawMaterialName;
    private BigDecimal quantityRequired;
    private String unit;
}
