package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateCommissionRuleRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.service.CommissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
public class CommissionController {
    
    private final CommissionService commissionService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> getAllCommissions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status) {
        
        PageResponse<Map<String, Object>> response = commissionService.getAllCommissions(page, limit, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markCommissionAsPaid(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = commissionService.markCommissionAsPaid(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Commission marked as paid"));
    }
    
    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCommissionRules() {
        List<Map<String, Object>> response = commissionService.getAllCommissionRules();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCommissionRule(@Valid @RequestBody CreateCommissionRuleRequest request) {
        Map<String, Object> response = commissionService.createCommissionRule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Commission rule created successfully"));
    }
    
    @PatchMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCommissionRule(
            @PathVariable String id,
            @RequestBody Map<String, String> updates) {
        Map<String, Object> response = commissionService.updateCommissionRule(id, updates);
        return ResponseEntity.ok(ApiResponse.success(response, "Commission rule updated successfully"));
    }
    
    @DeleteMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCommissionRule(@PathVariable String id) {
        commissionService.deleteCommissionRule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Commission rule deleted successfully"));
    }
}
