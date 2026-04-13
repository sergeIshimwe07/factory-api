package com.factory.factory_erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomItemRequest {
    
    @NotBlank(message = "Raw material ID is required")
    private String rawMaterialId;
    
    @NotNull(message = "Quantity required is required")
    @Positive(message = "Quantity required must be positive")
    private BigDecimal quantityRequired;
    
    @NotBlank(message = "Unit is required")
    private String unit;
}
