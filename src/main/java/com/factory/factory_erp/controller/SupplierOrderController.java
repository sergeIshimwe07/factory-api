package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateSupplierOrderRequest;
import com.factory.factory_erp.dto.request.UpdateSupplierOrderStatusRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.dto.response.SupplierOrderResponse;
import com.factory.factory_erp.service.SupplierOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/supplier-orders")
@RequiredArgsConstructor
public class SupplierOrderController {
    
    private final SupplierOrderService supplierOrderService;
    
    /**
     * Create a new supplier order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SupplierOrderResponse>> createSupplierOrder(
            @Valid @RequestBody CreateSupplierOrderRequest request) {
        
        SupplierOrderResponse response = supplierOrderService.createSupplierOrder(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Purchase order created successfully"));
    }
    
    /**
     * Get all supplier orders with filtering
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierOrderResponse>>> getAllSupplierOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search) {
        
        PageResponse<SupplierOrderResponse> response = supplierOrderService.getAllSupplierOrders(
                page, limit, status, supplierId, startDate, endDate, search
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Get supplier order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierOrderResponse>> getSupplierOrderById(
            @PathVariable String id) {
        
        SupplierOrderResponse response = supplierOrderService.getSupplierOrderById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Update supplier order status
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierOrderResponse>> updateSupplierOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateSupplierOrderStatusRequest request) {
        
        SupplierOrderResponse response = supplierOrderService.updateSupplierOrderStatus(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase order status updated"));
    }
    
    /**
     * Delete supplier order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplierOrder(
            @PathVariable String id) {
        
        supplierOrderService.deleteSupplierOrder(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Purchase order deleted successfully"));
    }
    
    /**
     * Upload proof of payment
     */
    @PostMapping("/{id}/proof-of-payment")
    public ResponseEntity<ApiResponse<SupplierOrderResponse>> uploadProofOfPayment(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        
        SupplierOrderResponse response = supplierOrderService.uploadProofOfPayment(id, file);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Proof of payment uploaded successfully"));
    }
    
    /**
     * Upload invoice
     */
    @PostMapping("/{id}/invoice")
    public ResponseEntity<ApiResponse<SupplierOrderResponse>> uploadInvoice(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        
        SupplierOrderResponse response = supplierOrderService.uploadInvoice(id, file);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice uploaded successfully"));
    }
}
