package com.factory.factory_erp.service;

import com.factory.factory_erp.entity.Product;
import com.factory.factory_erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final CommissionRepository commissionRepository;
    
    public Map<String, Object> getDashboardSummary(String period) {
        Map<String, Object> summary = new HashMap<>();
        
        BigDecimal totalSalesToday = salesOrderRepository.getTotalSalesToday();
        BigDecimal totalSalesMonth = salesOrderRepository.getTotalSalesThisMonth();
        BigDecimal outstandingCredit = salesOrderRepository.getTotalOutstandingCredit();
        
        summary.put("totalSalesToday", totalSalesToday != null ? totalSalesToday : BigDecimal.ZERO);
        summary.put("totalSalesMonth", totalSalesMonth != null ? totalSalesMonth : BigDecimal.ZERO);
        summary.put("outstandingCredit", outstandingCredit != null ? outstandingCredit : BigDecimal.ZERO);
        
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        List<Map<String, Object>> lowStockAlerts = lowStockProducts.stream()
                .limit(10)
                .map(product -> {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("productId", product.getProductId());
                    alert.put("productName", product.getName());
                    alert.put("currentStock", product.getCurrentStock());
                    alert.put("minimumStock", product.getMinimumStock());
                    alert.put("reorderLevel", product.getReorderLevel());
                    alert.put("status", product.getCurrentStock() < product.getMinimumStock() / 2 ? "critical" : "warning");
                    return alert;
                })
                .collect(Collectors.toList());
        
        summary.put("lowStockAlerts", lowStockAlerts);
        
        Map<String, Object> commissionSummary = new HashMap<>();
        BigDecimal paidCommissions = commissionRepository.getTotalPaidCommissions();
        BigDecimal unpaidCommissions = commissionRepository.getTotalUnpaidCommissions();
        
        commissionSummary.put("paid", paidCommissions != null ? paidCommissions : BigDecimal.ZERO);
        commissionSummary.put("unpaid", unpaidCommissions != null ? unpaidCommissions : BigDecimal.ZERO);
        commissionSummary.put("total", 
            (paidCommissions != null ? paidCommissions : BigDecimal.ZERO)
            .add(unpaidCommissions != null ? unpaidCommissions : BigDecimal.ZERO));
        
        summary.put("commissionSummary", commissionSummary);
        
        List<Map<String, Object>> salesTrend = generateSalesTrend();
        summary.put("salesTrend", salesTrend);
        
        return summary;
    }
    
    private List<Map<String, Object>> generateSalesTrend() {
        LocalDate today = LocalDate.now();
        return List.of(
            createTrendData(today.minusDays(5)),
            createTrendData(today.minusDays(4)),
            createTrendData(today.minusDays(3)),
            createTrendData(today.minusDays(2)),
            createTrendData(today.minusDays(1)),
            createTrendData(today)
        );
    }
    
    private Map<String, Object> createTrendData(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        BigDecimal revenue = salesOrderRepository.getTotalSalesBetweenDates(startOfDay, endOfDay);
        
        Map<String, Object> trend = new HashMap<>();
        trend.put("date", date.toString());
        trend.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
        trend.put("target", new BigDecimal("150000"));
        
        return trend;
    }
}
