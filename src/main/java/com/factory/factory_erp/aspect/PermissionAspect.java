package com.factory.factory_erp.aspect;

import com.factory.factory_erp.annotation.RequirePermission;
import com.factory.factory_erp.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    
    private final PermissionService permissionService;
    
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }
        
        String email = authentication.getName();
        String featureCode = requirePermission.feature();
        String permission = requirePermission.permission().name();
        
        boolean hasPermission = permissionService.hasPermission(email, featureCode, permission);
        
        if (!hasPermission) {
            throw new AccessDeniedException(
                String.format("User does not have %s permission for feature: %s", 
                    permission, featureCode)
            );
        }
    }
}
