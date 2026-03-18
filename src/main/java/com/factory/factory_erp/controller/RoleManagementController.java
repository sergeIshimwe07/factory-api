package com.factory.factory_erp.controller;

import com.factory.factory_erp.annotation.RequirePermission;
import com.factory.factory_erp.entity.RoleGroup;
import com.factory.factory_erp.service.PermissionService;
import com.factory.factory_erp.service.RoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleManagementController {
    
    private final RoleManagementService roleManagementService;
    private final PermissionService permissionService;
    
    @GetMapping
    @RequirePermission(feature = "ROLE_MANAGEMENT", permission = RequirePermission.PermissionType.READ)
    public ResponseEntity<List<RoleGroup>> getAllRoles() {
        return ResponseEntity.ok(roleManagementService.getAllRoles());
    }
    
    @PostMapping("/assign")
    @RequirePermission(feature = "ROLE_MANAGEMENT", permission = RequirePermission.PermissionType.EDIT)
    public ResponseEntity<Map<String, String>> assignRole(
            @RequestParam Long userId,
            @RequestParam String roleCode,
            Authentication authentication) {
        
        roleManagementService.assignRoleToUser(userId, roleCode, null);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Role assigned successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/remove")
    @RequirePermission(feature = "ROLE_MANAGEMENT", permission = RequirePermission.PermissionType.DELETE)
    public ResponseEntity<Map<String, String>> removeRole(
            @RequestParam Long userId,
            @RequestParam String roleCode) {
        
        roleManagementService.removeRoleFromUser(userId, roleCode);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Role removed successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    @RequirePermission(feature = "USER_MANAGEMENT", permission = RequirePermission.PermissionType.READ)
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(roleManagementService.getUserRoles(userId));
    }
    
    @GetMapping("/check-permission")
    public ResponseEntity<Map<String, Boolean>> checkPermission(
            @RequestParam String featureCode,
            @RequestParam String permission,
            Authentication authentication) {
        
        boolean hasPermission = permissionService.hasPermission(
                authentication.getName(), featureCode, permission);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasPermission", hasPermission);
        return ResponseEntity.ok(response);
    }
}
