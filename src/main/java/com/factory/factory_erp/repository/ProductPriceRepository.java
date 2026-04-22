package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ProductPrice entity.
 * Provides historical price tracking for products with decoupled price management.
 */
@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    
    /**
     * Find all prices for a product with a specific status
     */
    List<ProductPrice> findByProductIdAndStatus(Long productId, ProductPrice.PriceStatus status);
    
    /**
     * Find all prices for a product, ordered by start date descending
     */
    List<ProductPrice> findByProductIdOrderByStartDateDesc(Long productId);
    
    /**
     * Find all prices for a product within a date range
     */
    @Query("SELECT p FROM ProductPrice p WHERE p.productId = :productId " +
           "AND p.startDate <= :endDate " +
           "AND (p.endDate IS NULL OR p.endDate >= :startDate)")
    List<ProductPrice> findByProductIdAndPeriod(
        @Param("productId") Long productId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Find the active price for a product on a specific date
     */
    @Query("SELECT p FROM ProductPrice p WHERE p.productId = :productId " +
           "AND p.status = 'ACTIVE' " +
           "AND p.startDate <= :date " +
           "AND (p.endDate IS NULL OR p.endDate >= :date)")
    Optional<ProductPrice> findActivePriceOn(
        @Param("productId") Long productId,
        @Param("date") LocalDate date
    );
}
