package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.ProductionEntry;
import com.factory.factory_erp.entity.enums.ProductionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProductionEntryRepository extends JpaRepository<ProductionEntry, Long> {
    
    Optional<ProductionEntry> findByProductionId(String productionId);
    
    Optional<ProductionEntry> findByBatchNumber(String batchNumber);
    
    Page<ProductionEntry> findByStatus(ProductionStatus status, Pageable pageable);
    
    @Query("SELECT pe FROM ProductionEntry pe WHERE pe.startDate >= :startDate AND pe.startDate <= :endDate")
    Page<ProductionEntry> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate, 
                                          Pageable pageable);
}
