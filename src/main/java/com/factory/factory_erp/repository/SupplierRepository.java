package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.Supplier;
import com.factory.factory_erp.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    Optional<Supplier> findBySupplierId(String supplierId);
    
    Page<Supplier> findByStatus(Status status, Pageable pageable);
}
