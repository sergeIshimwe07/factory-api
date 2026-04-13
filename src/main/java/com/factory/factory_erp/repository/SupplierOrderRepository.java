package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.SupplierOrder;
import com.factory.factory_erp.entity.enums.SupplierOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {
    
    Optional<SupplierOrder> findBySupplierOrderId(String supplierOrderId);
    
    Page<SupplierOrder> findByStatus(SupplierOrderStatus status, Pageable pageable);
    
    Page<SupplierOrder> findBySupplierId(Long supplierId, Pageable pageable);
    
    @Query("SELECT so FROM SupplierOrder so WHERE " +
           "(:status IS NULL OR so.status = :status) " +
           "AND (:supplierId IS NULL OR so.supplier.id = :supplierId) " +
           "AND (:startDate IS NULL OR so.deliveryDate >= :startDate) " +
           "AND (:endDate IS NULL OR so.deliveryDate <= :endDate) " +
           "AND (:search IS NULL OR LOWER(so.supplier.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(so.supplierOrderId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SupplierOrder> findWithFilters(
            @Param("status") SupplierOrderStatus status,
            @Param("supplierId") Long supplierId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("search") String search,
            Pageable pageable
    );
    
    long countByStatus(SupplierOrderStatus status);
}
