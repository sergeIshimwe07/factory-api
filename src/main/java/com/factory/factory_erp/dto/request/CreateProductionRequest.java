package com.factory.factory_erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductionRequest {
    
    @NotBlank(message = "BOM ID is required")
    private String billOfMaterialsId;
    
    @NotNull(message = "Quantity produced is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantityProduced;
    
    private Integer quantityDefective;
    private String supervisor;
    private String notes;
}
