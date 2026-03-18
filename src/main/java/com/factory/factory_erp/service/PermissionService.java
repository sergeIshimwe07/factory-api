package com.factory.factory_erp.service;

import com.factory.factory_erp.entity.RolePermission;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.repository.RolePermissionRepository;
import com.factory.factory_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;
    
    public boolean hasPermission(String email, String featureCode, String permission) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Long> roleIds = user.getUserRoles().stream()
                .filter(userRole -> userRole.getIsActive())
                .map(userRole -> userRole.getRole().getId())
                .collect(Collectors.toList());
        
        if (roleIds.isEmpty()) {
            return false;
        }
        
        List<RolePermission> permissions = rolePermissionRepository
                .findByRoleIdsAndFeatureCode(roleIds, featureCode);
        
        return permissions.stream().anyMatch(rp -> {
            switch (permission.toLowerCase()) {
                case "read":
                    return rp.getCanRead();
                case "edit":
                    return rp.getCanEdit();
                case "delete":
                    return rp.getCanDelete();
                default:
                    return false;
            }
        });
    }
    
    public boolean canRead(String email, String featureCode) {
        return hasPermission(email, featureCode, "read");
    }
    
    public boolean canEdit(String email, String featureCode) {
        return hasPermission(email, featureCode, "edit");
    }
    
    public boolean canDelete(String email, String featureCode) {
        return hasPermission(email, featureCode, "delete");
    }
}
