package com.factory.factory_erp.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierOrderRequest {
    
    @NotBlank(message = "Supplier ID is required")
    private String supplierId;
    
    @FutureOrPresent(message = "Delivery date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    
    private String notes;
    
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<SupplierOrderItemRequest> items;
}
