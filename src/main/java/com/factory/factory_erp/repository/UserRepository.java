package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUserId(String userId);
    
    boolean existsByEmail(String email);
    
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
}
