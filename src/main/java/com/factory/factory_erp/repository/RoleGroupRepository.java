package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.RoleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleGroupRepository extends JpaRepository<RoleGroup, Long> {
    Optional<RoleGroup> findByRoleCode(Integer roleCode);
    boolean existsByRoleCode(Integer roleCode);
}
