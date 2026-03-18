package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateSupplierRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    
    private final SupplierService supplierService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> getAllSuppliers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        PageResponse<Map<String, Object>> response = supplierService.getAllSuppliers(page, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        Map<String, Object> response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Supplier created successfully"));
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateSupplier(
            @PathVariable String id,
            @RequestBody Map<String, String> updates) {
        Map<String, Object> response = supplierService.updateSupplier(id, updates);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier updated successfully"));
    }
}
