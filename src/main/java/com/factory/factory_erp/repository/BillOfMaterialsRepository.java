package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.BillOfMaterials;
import com.factory.factory_erp.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillOfMaterialsRepository extends JpaRepository<BillOfMaterials, Long> {
    
    Optional<BillOfMaterials> findByBomId(String bomId);
    
    Optional<BillOfMaterials> findByFinishedProductId(Long finishedProductId);
    
    Page<BillOfMaterials> findByStatus(Status status, Pageable pageable);
}
