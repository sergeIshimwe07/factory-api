package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportingService reportingService;
    
    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSalesReport(
            @RequestParam String dateFrom,
            @RequestParam String dateTo) {
        Map<String, Object> report = reportingService.getSalesReport(dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
    
    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryReport(
            @RequestParam(required = false) String category) {
        Map<String, Object> report = reportingService.getInventoryReport(category);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
    
    @GetMapping("/production")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductionReport(
            @RequestParam String dateFrom,
            @RequestParam String dateTo) {
        Map<String, Object> report = reportingService.getProductionReport(dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
    
    @GetMapping("/commissions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCommissionsReport(
            @RequestParam String month) {
        Map<String, Object> report = reportingService.getCommissionsReport(month);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
    
    @GetMapping("/profit-loss")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfitLossReport(
            @RequestParam String dateFrom,
            @RequestParam String dateTo) {
        Map<String, Object> report = reportingService.getProfitLossReport(dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
    
    @GetMapping("/balance-sheet")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalanceSheet(
            @RequestParam String asOfDate) {
        Map<String, Object> report = reportingService.getBalanceSheet(asOfDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
    
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomersReport(
            @RequestParam String dateFrom,
            @RequestParam String dateTo) {
        Map<String, Object> report = reportingService.getCustomersReport(dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
