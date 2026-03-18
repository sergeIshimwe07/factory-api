package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.Customer;
import com.factory.factory_erp.entity.enums.CustomerType;
import com.factory.factory_erp.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByCustomerId(String customerId);
    
    Page<Customer> findByType(CustomerType type, Pageable pageable);
    
    Page<Customer> findByStatus(Status status, Pageable pageable);
    
    Page<Customer> findByIsBlocked(Boolean isBlocked, Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.status = :status AND c.isBlocked = false")
    Page<Customer> findActiveCustomers(@Param("status") Status status, Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer> searchCustomers(@Param("search") String search, Pageable pageable);
}
