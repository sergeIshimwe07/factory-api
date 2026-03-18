package com.factory.factory_erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockMovementRequest {
    
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @NotBlank(message = "Movement type is required")
    private String type;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    private String reference;
    private String notes;
}
