package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.RolePermission;
import com.factory.factory_erp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUserId(String userId);
    
    boolean existsByEmail(String email);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userRoles ur " +
           "LEFT JOIN FETCH ur.role rg " +
           "LEFT JOIN FETCH rg.permissions rp " +
           "LEFT JOIN FETCH rp.feature f " +
           "WHERE u.id = :userId")
    Optional<User> findByIdWithRolesAndPermissions(Long userId);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userRoles ur " +
           "LEFT JOIN FETCH ur.role rg " +
           "LEFT JOIN FETCH rg.permissions rp " +
           "LEFT JOIN FETCH rp.feature f " +
           "WHERE u.email = :email")
    Optional<User> findByEmailWithRolesAndPermissions(String email);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userRoles ur " +
           "LEFT JOIN FETCH ur.role rg " +
           "WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userRoles ur " +
           "LEFT JOIN FETCH ur.role rg " +
           "LEFT JOIN FETCH rg.permissions rp " +
           "LEFT JOIN FETCH rp.feature f " +
           "WHERE u.userId = :userId")
    Optional<User> findByUserIdWithRolesAndPermissions(String userId);

}
