package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateSalesOrderRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.dto.response.SalesOrderResponse;
import com.factory.factory_erp.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesOrderController {
    
    private final SalesOrderService salesOrderService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SalesOrderResponse>>> getAllSalesOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status) {
        
        PageResponse<SalesOrderResponse> response = salesOrderService.getAllSalesOrders(page, limit, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> getSalesOrderById(@PathVariable String id) {
        SalesOrderResponse response = salesOrderService.getSalesOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<SalesOrderResponse>> createSalesOrder(@Valid @RequestBody CreateSalesOrderRequest request) {
        SalesOrderResponse response = salesOrderService.createSalesOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Sales order created successfully"));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> updateOrderStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        SalesOrderResponse response = salesOrderService.updateOrderStatus(id, request.get("status"));
        return ResponseEntity.ok(ApiResponse.success(response, "Order status updated successfully"));
    }
}
