package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateStockMovementRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @GetMapping("/movements")
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> getStockMovements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dateFrom) {
        
        PageResponse<Map<String, Object>> response = inventoryService.getStockMovements(page, limit, type, dateFrom);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/movements")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createStockMovement(
            @Valid @RequestBody CreateStockMovementRequest request) {
        Map<String, Object> response = inventoryService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Stock movement recorded successfully"));
    }
}
