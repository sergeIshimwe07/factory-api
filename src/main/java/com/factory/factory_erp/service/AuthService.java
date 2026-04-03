package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.LoginRequest;
import com.factory.factory_erp.dto.response.LoginResponse;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.repository.UserRepository;
import com.factory.factory_erp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            log.debug("Authenticating user with Spring Security");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("User authenticated successfully: {}", request.getEmail());
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for email: {} - Reason: {}", 
                request.getEmail(), e.getMessage());
            throw new RuntimeException("Invalid email or password", e);
        }
        
        log.debug("Fetching user details with roles and permissions for: {}", request.getEmail());
        User user = userRepository.findByEmailWithRolesAndPermissions(request.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found after successful authentication: {}", request.getEmail());
                    return new RuntimeException("User not found");
                });
        
        log.info("Building login response for user: {} (ID: {})", user.getEmail(), user.getUserId());
        return buildLoginResponseAndUpdateLastLogin(user);
    }

    public LoginResponse signup(String name, String email, String rawPassword, Long role, 
                                String firstName, String lastName) {
        log.info("Signup attempt for email: {}", email);
        
        if (userRepository.existsByEmail(email)) {
            log.warn("Signup failed - Email already exists: {}", email);
            throw new IllegalArgumentException("Email already exists");
        }

        log.debug("Creating new user entity for: {}", email);
        User user = User.builder()
                .names(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .firstName(firstName)
                .lastName(lastName)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user created successfully: {} (ID: {})", savedUser.getEmail(), savedUser.getUserId());
        
        return buildLoginResponseAndUpdateLastLogin(savedUser);
    }

    private LoginResponse buildLoginResponseAndUpdateLastLogin(User user) {
        log.debug("Processing roles and permissions for user: {}", user.getEmail());
        
        // Get active user roles
        List<com.factory.factory_erp.entity.UserRole> activeUserRoles = user.getUserRoles().stream()
                .filter(userRole -> Boolean.TRUE.equals(userRole.getIsActive()))
                .collect(Collectors.toList());

        log.debug("Found {} active roles for user: {}", activeUserRoles.size(), user.getEmail());
        
        // Validate that user has at least one role with permissions
        if (activeUserRoles.isEmpty()) {
            log.error("User {} has no active roles assigned", user.getEmail());
            throw new IllegalArgumentException("User does not have any assigned roles. Please contact administrator.");
        }

        // Log role details for debugging
        activeUserRoles.forEach(userRole -> {
            log.debug("User {} has role: {} (ID: {})", 
                user.getEmail(), 
                userRole.getRole().getRoleCode(), 
                userRole.getRole().getId());
        });

        // Check if user has at least one permission
        boolean hasPermissions = activeUserRoles.stream()
                .flatMap(userRole -> userRole.getRole().getPermissions().stream())
                .findAny()
                .isPresent();

        if (!hasPermissions) {
            log.error("User {} has roles but no permissions assigned", user.getEmail());
            throw new IllegalArgumentException("User does not have any permissions assigned to their roles. Please contact administrator.");
        }

        log.debug("Updating last login timestamp for user: {}", user.getEmail());
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Build role info objects
        List<LoginResponse.RoleInfo> roleInfos = activeUserRoles.stream()
                .map(userRole -> {
                    log.trace("Building role info for role: {}", userRole.getRole().getRoleCode());
                    return LoginResponse.RoleInfo.builder()
                            .id(userRole.getRole().getId())
                            .roleCode(userRole.getRole().getRoleCode())
                            .roleName(userRole.getRole().getRoleName())
                            .description(userRole.getRole().getDescription())
                            .build();
                })
                .collect(Collectors.toList());

        // Build permission info objects
        List<LoginResponse.PermissionInfo> permissionInfos = activeUserRoles.stream()
                .flatMap(userRole -> userRole.getRole().getPermissions().stream())
                .map(rolePermission -> {
                    log.trace("Building permission for feature: {}, user: {}", 
                        rolePermission.getFeature().getFeatureCode(), 
                        user.getEmail());
                    return LoginResponse.PermissionInfo.builder()
                            .id(rolePermission.getId())
                            .featureCode(rolePermission.getFeature().getFeatureCode())
                            .featureName(rolePermission.getFeature().getFeatureName())
                            .module(rolePermission.getFeature().getModule())
                            .canRead(rolePermission.getCanRead())
                            .canEdit(rolePermission.getCanEdit())
                            .canDelete(rolePermission.getCanDelete())
                            .build();
                })
                .distinct()
                .collect(Collectors.toList());

        log.info("User {} has {} roles and {} permissions", 
            user.getEmail(), roleInfos.size(), permissionInfos.size());

        // Extract role codes for JWT claims
        List<String> roleCodes = roleInfos.stream()
                .map(LoginResponse.RoleInfo::getRoleCode)
                .collect(Collectors.toList());

        // Extract permission strings for JWT claims
        List<String> permissionStrings = permissionInfos.stream()
                .flatMap(perm -> {
                    List<String> perms = new ArrayList<>();
                    if (Boolean.TRUE.equals(perm.getCanRead())) {
                        perms.add(perm.getFeatureCode() + ":READ");
                    }
                    if (Boolean.TRUE.equals(perm.getCanEdit())) {
                        perms.add(perm.getFeatureCode() + ":EDIT");
                    }
                    if (Boolean.TRUE.equals(perm.getCanDelete())) {
                        perms.add(perm.getFeatureCode() + ":DELETE");
                    }
                    return perms.stream();
                })
                .distinct()
                .collect(Collectors.toList());

        log.debug("Generated {} permission strings for JWT claims", permissionStrings.size());

        // Build JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("name", user.getNames());
        claims.put("email", user.getEmail());
        claims.put("avatar", user.getAvatar());
        claims.put("roles", roleCodes);
        claims.put("permissions", permissionStrings);

        String token = jwtUtil.generateToken(user.getEmail(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), claims);
        
        log.info("JWT tokens generated successfully for user: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getUserId())
                        .name(user.getNames())
                        .email(user.getEmail())
                        .avatar(user.getAvatar())
                        .roles(roleInfos)
                        .permissions(permissionInfos)
                        .build())
                .build();
    }
}