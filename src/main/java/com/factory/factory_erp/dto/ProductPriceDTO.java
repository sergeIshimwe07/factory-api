package com.factory.factory_erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.factory.factory_erp.entity.ProductPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for ProductPrice entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceDTO {
    
    private Long id;
    
    private Long productId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private BigDecimal amount;
    
    private String currency;
    
    private String status;
    
    private String notes;
    
    private String operatorName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime updatedAt;
    
    /**
     * Convert ProductPrice entity to DTO
     */
    public static ProductPriceDTO fromEntity(ProductPrice productPrice) {
        return ProductPriceDTO.builder()
            .id(productPrice.getId())
            .productId(productPrice.getProductId())
            .startDate(productPrice.getStartDate())
            .endDate(productPrice.getEndDate())
            .amount(productPrice.getAmount())
            .currency(productPrice.getCurrency())
            .status(productPrice.getStatus().toString())
            .notes(productPrice.getNotes())
            .operatorName(productPrice.getOperator() != null ? productPrice.getOperator().getNames() : null)
            .createdAt(productPrice.getCreatedAt())
            .updatedAt(productPrice.getUpdatedAt())
            .build();
    }
}
