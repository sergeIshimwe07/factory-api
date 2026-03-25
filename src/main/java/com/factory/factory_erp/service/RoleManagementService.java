package com.factory.factory_erp.service;

import com.factory.factory_erp.entity.RoleGroup;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.entity.UserRole;
import com.factory.factory_erp.repository.RoleGroupRepository;
import com.factory.factory_erp.repository.UserRepository;
import com.factory.factory_erp.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleManagementService {
    
    private final UserRepository userRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final UserRoleRepository userRoleRepository;
    
    @Transactional
    public void assignRoleToUser(Long userId, Long roleCode, Long assignedByUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        RoleGroup role = roleGroupRepository.findById(roleCode)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        User assignedBy = assignedByUserId != null ? 
                userRepository.findById(assignedByUserId).orElse(null) : null;
        
        boolean alreadyAssigned = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getId().equals(role.getId()));
        
        if (!alreadyAssigned) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setAssignedBy(assignedBy);
            userRole.setIsActive(true);
            
            userRoleRepository.save(userRole);
        }
    }
    
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        RoleGroup role = roleGroupRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        userRoleRepository.deleteByUserIdAndRoleId(userId, role.getId());
    }
    
    public List<String> getUserRoles(Long userId) {
        return userRoleRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(ur -> ur.getRole().getRoleCode())
                .collect(Collectors.toList());
    }
    
    public List<RoleGroup> getAllRoles() {
        return roleGroupRepository.findAll();
    }
}
