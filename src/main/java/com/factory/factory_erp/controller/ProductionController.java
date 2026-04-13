package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateBOMRequest;
import com.factory.factory_erp.dto.request.CreateProductionRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.BomResponse;
import com.factory.factory_erp.dto.response.PaginatedBomResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.service.ProductionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {
    
    private final ProductionService productionService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> getAllProductions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status) {
        
        PageResponse<Map<String, Object>> response = productionService.getAllProductions(page, limit, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProduction(@Valid @RequestBody CreateProductionRequest request) {
        Map<String, Object> response = productionService.createProduction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Production entry created successfully"));
    }
    
    @GetMapping("/bom")
    public ResponseEntity<ApiResponse<PaginatedBomResponse>> getAllBOMs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        PaginatedBomResponse response = productionService.getAllBOMs(page, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/bom")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createBOM(@Valid @RequestBody CreateBOMRequest request) {
        Map<String, Object> response = productionService.createBOM(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "BOM created successfully"));
    }
    
    @GetMapping("/bom/{finishedProductId}")
    public ResponseEntity<ApiResponse<BomResponse>> getBOMByFinishedProductId(
            @PathVariable String finishedProductId) {
        
        BomResponse response = productionService.getBOMByFinishedProductId(finishedProductId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
