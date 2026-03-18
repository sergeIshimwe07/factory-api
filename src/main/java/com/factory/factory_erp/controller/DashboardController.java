package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary(
            @RequestParam(required = false, defaultValue = "month") String period) {
        Map<String, Object> summary = dashboardService.getDashboardSummary(period);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
