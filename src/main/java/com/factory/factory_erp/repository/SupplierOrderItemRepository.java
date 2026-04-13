package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.SupplierOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierOrderItemRepository extends JpaRepository<SupplierOrderItem, Long> {
    
    List<SupplierOrderItem> findBySupplierOrderId(Long supplierOrderId);
}
