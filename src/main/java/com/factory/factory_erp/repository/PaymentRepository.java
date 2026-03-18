package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentId(String paymentId);
    
    List<Payment> findBySalesOrderId(Long salesOrderId);
    
    Page<Payment> findBySalesOrderCustomerId(Long customerId, Pageable pageable);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.salesOrder.id = :salesOrderId")
    BigDecimal getTotalPaidForOrder(@Param("salesOrderId") Long salesOrderId);
}
