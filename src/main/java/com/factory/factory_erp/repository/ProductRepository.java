package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.Product;
import com.factory.factory_erp.entity.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByProductId(String productId);
    
    Optional<Product> findBySku(String sku);
    
    Page<Product> findByCategory(String category, Pageable pageable);
    
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.currentStock < p.minimumStock")
    List<Product> findLowStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.currentStock < p.reorderLevel")
    List<Product> findProductsNeedingReorder();
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findAllCategories();
}
