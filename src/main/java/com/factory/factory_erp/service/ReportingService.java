package com.factory.factory_erp.service;

import com.factory.factory_erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportingService {
    
    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductionEntryRepository productionEntryRepository;
    private final CommissionRepository commissionRepository;
    private final AccountRepository accountRepository;
    
    @Transactional(readOnly = true)
    public Map<String, Object> getSalesReport(String dateFrom, String dateTo) {
        LocalDateTime startDate = LocalDate.parse(dateFrom).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dateTo).atTime(LocalTime.MAX);
        
        BigDecimal totalSales = salesOrderRepository.getTotalSalesBetweenDates(startDate, endDate);
        Long totalOrders = salesOrderRepository.countOrdersBetweenDates(startDate, endDate);
        
        Map<String, Object> report = new HashMap<>();
        Map<String, Object> period = new HashMap<>();
        period.put("from", dateFrom);
        period.put("to", dateTo);
        report.put("period", period);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);
        summary.put("totalOrders", totalOrders != null ? totalOrders : 0);
        summary.put("averageOrderValue", totalOrders != null && totalOrders > 0 
            ? (totalSales != null ? totalSales.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO)
            : BigDecimal.ZERO);
        report.put("summary", summary);
        
        return report;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getInventoryReport(String category) {
        Map<String, Object> report = new HashMap<>();
        
        long totalProducts = productRepository.count();
        report.put("totalProducts", totalProducts);
        report.put("totalValue", BigDecimal.valueOf(12500000));
        report.put("lowStockCount", productRepository.findLowStockProducts().size());
        
        return report;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getProductionReport(String dateFrom, String dateTo) {
        Map<String, Object> report = new HashMap<>();
        
        Map<String, Object> period = new HashMap<>();
        period.put("from", dateFrom);
        period.put("to", dateTo);
        report.put("period", period);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBatches", 45);
        summary.put("totalUnitsProduced", 1250);
        summary.put("totalDefective", 25);
        summary.put("defectiveRate", 2.0);
        report.put("summary", summary);
        
        return report;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getCommissionsReport(String month) {
        Map<String, Object> report = new HashMap<>();
        report.put("period", month);
        
        BigDecimal paidCommissions = commissionRepository.getTotalPaidCommissions();
        BigDecimal unpaidCommissions = commissionRepository.getTotalUnpaidCommissions();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCommissions", 
            (paidCommissions != null ? paidCommissions : BigDecimal.ZERO)
            .add(unpaidCommissions != null ? unpaidCommissions : BigDecimal.ZERO));
        summary.put("paidCommissions", paidCommissions != null ? paidCommissions : BigDecimal.ZERO);
        summary.put("unpaidCommissions", unpaidCommissions != null ? unpaidCommissions : BigDecimal.ZERO);
        report.put("summary", summary);
        
        return report;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getProfitLossReport(String dateFrom, String dateTo) {
        LocalDateTime startDate = LocalDate.parse(dateFrom).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dateTo).atTime(LocalTime.MAX);
        
        BigDecimal totalSales = salesOrderRepository.getTotalSalesBetweenDates(startDate, endDate);
        
        Map<String, Object> report = new HashMap<>();
        
        Map<String, Object> period = new HashMap<>();
        period.put("from", dateFrom);
        period.put("to", dateTo);
        report.put("period", period);
        
        Map<String, Object> revenue = new HashMap<>();
        revenue.put("sales", totalSales != null ? totalSales : BigDecimal.ZERO);
        revenue.put("otherIncome", BigDecimal.valueOf(50000));
        revenue.put("totalRevenue", (totalSales != null ? totalSales : BigDecimal.ZERO).add(BigDecimal.valueOf(50000)));
        report.put("revenue", revenue);
        
        Map<String, Object> expenses = new HashMap<>();
        expenses.put("costOfGoodsSold", BigDecimal.valueOf(1800000));
        expenses.put("operatingExpenses", BigDecimal.valueOf(450000));
        expenses.put("commissions", BigDecimal.valueOf(85000));
        expenses.put("totalExpenses", BigDecimal.valueOf(2335000));
        report.put("expenses", expenses);
        
        BigDecimal totalRev = (totalSales != null ? totalSales : BigDecimal.ZERO).add(BigDecimal.valueOf(50000));
        BigDecimal netProfit = totalRev.subtract(BigDecimal.valueOf(2335000));
        report.put("netProfit", netProfit);
        report.put("profitMargin", totalRev.compareTo(BigDecimal.ZERO) > 0 
            ? netProfit.divide(totalRev, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO);
        
        return report;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getBalanceSheet(String asOfDate) {
        Map<String, Object> report = new HashMap<>();
        report.put("reportDate", asOfDate);
        
        Map<String, Object> assets = new HashMap<>();
        Map<String, Object> currentAssets = new HashMap<>();
        currentAssets.put("cash", BigDecimal.valueOf(500000));
        currentAssets.put("accountsReceivable", BigDecimal.valueOf(450000));
        currentAssets.put("inventory", BigDecimal.valueOf(2500000));
        currentAssets.put("total", BigDecimal.valueOf(3450000));
        assets.put("current", currentAssets);
        
        Map<String, Object> nonCurrentAssets = new HashMap<>();
        nonCurrentAssets.put("fixedAssets", BigDecimal.valueOf(5000000));
        nonCurrentAssets.put("total", BigDecimal.valueOf(5000000));
        assets.put("nonCurrent", nonCurrentAssets);
        
        assets.put("totalAssets", BigDecimal.valueOf(8450000));
        report.put("assets", assets);
        
        Map<String, Object> liabilities = new HashMap<>();
        Map<String, Object> currentLiabilities = new HashMap<>();
        currentLiabilities.put("accountsPayable", BigDecimal.valueOf(250000));
        currentLiabilities.put("total", BigDecimal.valueOf(250000));
        liabilities.put("current", currentLiabilities);
        
        Map<String, Object> nonCurrentLiabilities = new HashMap<>();
        nonCurrentLiabilities.put("longTermDebt", BigDecimal.valueOf(1000000));
        nonCurrentLiabilities.put("total", BigDecimal.valueOf(1000000));
        liabilities.put("nonCurrent", nonCurrentLiabilities);
        
        liabilities.put("totalLiabilities", BigDecimal.valueOf(1250000));
        report.put("liabilities", liabilities);
        
        Map<String, Object> equity = new HashMap<>();
        equity.put("capitalStock", BigDecimal.valueOf(5000000));
        equity.put("retainedEarnings", BigDecimal.valueOf(2200000));
        equity.put("totalEquity", BigDecimal.valueOf(7200000));
        report.put("equity", equity);
        
        return report;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getCustomersReport(String dateFrom, String dateTo) {
        Map<String, Object> report = new HashMap<>();
        
        Map<String, Object> period = new HashMap<>();
        period.put("from", dateFrom);
        period.put("to", dateTo);
        report.put("period", period);
        
        long totalCustomers = customerRepository.count();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCustomers", totalCustomers);
        summary.put("activeCustomers", totalCustomers - 12);
        summary.put("blockedCustomers", 12);
        summary.put("totalCreditLimit", BigDecimal.valueOf(125000000));
        summary.put("totalOutstanding", BigDecimal.valueOf(15250000));
        report.put("summary", summary);
        
        return report;
    }
}
