package com.factory.factory_erp.repository;

import com.factory.factory_erp.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    List<RolePermission> findByRoleId(Long roleId);
    
    Optional<RolePermission> findByRoleIdAndFeatureId(Long roleId, Long featureId);
    
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.id IN :roleIds AND rp.feature.featureCode = :featureCode")
    List<RolePermission> findByRoleIdsAndFeatureCode(@Param("roleIds") List<Long> roleIds, @Param("featureCode") String featureCode);
}
