package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.StockMovement;
import com.factory.factory_erp.entity.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    Optional<StockMovement> findByMovementId(String movementId);
    
    Page<StockMovement> findByProductId(Long productId, Pageable pageable);
    
    Page<StockMovement> findByType(MovementType type, Pageable pageable);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt >= :startDate AND sm.createdAt <= :endDate")
    Page<StockMovement> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate, 
                                         Pageable pageable);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.type = :type AND sm.createdAt >= :startDate")
    Page<StockMovement> findByTypeAndDateAfter(@Param("type") MovementType type, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                Pageable pageable);
}
