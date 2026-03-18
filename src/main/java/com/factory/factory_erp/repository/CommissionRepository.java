package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.Commission;
import com.factory.factory_erp.entity.enums.CommissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    
    Optional<Commission> findByCommissionId(String commissionId);
    
    Page<Commission> findByStatus(CommissionStatus status, Pageable pageable);
    
    Page<Commission> findByAgentId(Long agentId, Pageable pageable);
    
    Page<Commission> findByPeriod(String period, Pageable pageable);
    
    @Query("SELECT SUM(c.commission) FROM Commission c WHERE c.status = :status")
    BigDecimal getTotalCommissionsByStatus(@Param("status") CommissionStatus status);
    
    @Query("SELECT SUM(c.commission) FROM Commission c WHERE c.status = 'paid'")
    BigDecimal getTotalPaidCommissions();
    
    @Query("SELECT SUM(c.commission) FROM Commission c WHERE c.status = 'unpaid'")
    BigDecimal getTotalUnpaidCommissions();
}
