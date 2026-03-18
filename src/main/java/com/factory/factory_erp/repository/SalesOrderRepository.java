package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.SalesOrder;
import com.factory.factory_erp.entity.enums.OrderStatus;
import com.factory.factory_erp.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    
    Optional<SalesOrder> findBySaleId(String saleId);
    
    Optional<SalesOrder> findByOrderNumber(String orderNumber);
    
    Page<SalesOrder> findByStatus(OrderStatus status, Pageable pageable);
    
    Page<SalesOrder> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    
    Page<SalesOrder> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<SalesOrder> findByAgentId(Long agentId, Pageable pageable);
    
    @Query("SELECT SUM(s.total) FROM SalesOrder s WHERE s.orderDate >= :startDate AND s.orderDate <= :endDate")
    BigDecimal getTotalSalesBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(s) FROM SalesOrder s WHERE s.orderDate >= :startDate AND s.orderDate <= :endDate")
    Long countOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(s.total) FROM SalesOrder s WHERE DATE(s.orderDate) = CURRENT_DATE")
    BigDecimal getTotalSalesToday();
    
    @Query("SELECT SUM(s.total) FROM SalesOrder s WHERE MONTH(s.orderDate) = MONTH(CURRENT_DATE) " +
           "AND YEAR(s.orderDate) = YEAR(CURRENT_DATE)")
    BigDecimal getTotalSalesThisMonth();
    
    @Query("SELECT SUM(s.total) FROM SalesOrder s WHERE s.paymentStatus != 'paid'")
    BigDecimal getTotalOutstandingCredit();
}
