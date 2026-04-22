package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.ProductPriceDTO;
import com.factory.factory_erp.entity.ProductPrice;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.repository.UserRepository;
import com.factory.factory_erp.service.ProductPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing product prices with historical tracking.
 * Maintains price history to ensure reports and past records retain accurate historical data.
 */
@RestController
@RequestMapping("/api/product-prices")
@RequiredArgsConstructor
@Tag(name = "Product Prices", description = "Manage product price history with decoupled tracking")
public class ProductPriceController {
    
    private final ProductPriceService productPriceService;
    private final UserRepository userRepository;
    
    @GetMapping("/product/{productId}/current")
    @Operation(summary = "Get current price for a product")
    public ResponseEntity<?> getCurrentPrice(
        @Parameter(description = "Product ID") @PathVariable Long productId) {
        return productPriceService.getCurrentPrice(productId)
            .map(price -> ResponseEntity.ok(ProductPriceDTO.fromEntity(price)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/product/{productId}/on-date")
    @Operation(summary = "Get price for a product on a specific date")
    public ResponseEntity<?> getPriceOnDate(
        @Parameter(description = "Product ID") @PathVariable Long productId,
        @Parameter(description = "Date in yyyy-MM-dd format") 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return productPriceService.getActivePriceOn(productId, date)
            .map(price -> ResponseEntity.ok(ProductPriceDTO.fromEntity(price)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/product/{productId}/history")
    @Operation(summary = "Get price change history for a product")
    public ResponseEntity<List<ProductPriceDTO>> getPriceHistory(
        @Parameter(description = "Product ID") @PathVariable Long productId) {
        List<ProductPriceDTO> history = productPriceService.getPriceChangeHistory(productId)
            .stream()
            .map(ProductPriceDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/product/{productId}/period")
    @Operation(summary = "Get prices for a product within a date range")
    public ResponseEntity<List<ProductPriceDTO>> getPricesForPeriod(
        @Parameter(description = "Product ID") @PathVariable Long productId,
        @Parameter(description = "Start date in yyyy-MM-dd format") 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date in yyyy-MM-dd format") 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ProductPriceDTO> prices = productPriceService.getPriceHistoryForPeriod(productId, startDate, endDate)
            .stream()
            .map(ProductPriceDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(prices);
    }
    
    @PostMapping
    @Operation(summary = "Create a new price for a product")
    public ResponseEntity<ProductPriceDTO> createPrice(
        @RequestBody CreatePriceRequest request) {
        User operator = userRepository.findById(request.getOperatorId())
            .orElseThrow(() -> new IllegalArgumentException("Operator not found"));
        
        ProductPrice newPrice = productPriceService.createPrice(
            request.getProductId(),
            request.getAmount(),
            request.getCurrency(),
            operator,
            request.getStartDate(),
            request.getNotes()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductPriceDTO.fromEntity(newPrice));
    }
    
    @GetMapping("/product/{productId}/archived")
    @Operation(summary = "Get archived prices for a product")
    public ResponseEntity<List<ProductPriceDTO>> getArchivedPrices(
        @Parameter(description = "Product ID") @PathVariable Long productId) {
        List<ProductPriceDTO> archived = productPriceService.getArchivedPrices(productId)
            .stream()
            .map(ProductPriceDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(archived);
    }
    
    /**
     * Request body for creating a new price
     */
    @lombok.Data
    public static class CreatePriceRequest {
        private Long productId;
        private java.math.BigDecimal amount;
        private String currency;
        private Long operatorId;
        private LocalDate startDate;
        private String notes;
    }
}
