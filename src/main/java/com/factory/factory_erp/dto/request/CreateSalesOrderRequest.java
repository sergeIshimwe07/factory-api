package com.factory.factory_erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalesOrderRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotEmpty(message = "Order items are required")
    @Valid
    private List<OrderItemRequest> items;
    
    private BigDecimal discount;
    private String notes;
    private LocalDate dueDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        
        @NotBlank(message = "Product ID is required")
        private String productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be positive")
        private BigDecimal unitPrice;
        
        private BigDecimal discount;
    }
}
