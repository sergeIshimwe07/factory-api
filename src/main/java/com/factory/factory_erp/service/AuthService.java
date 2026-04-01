package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.LoginRequest;
import com.factory.factory_erp.dto.response.LoginResponse;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.entity.RolePermission;
import com.factory.factory_erp.repository.RolePermissionRepository;
import com.factory.factory_erp.repository.UserRepository;
import com.factory.factory_erp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildLoginResponseAndUpdateLastLogin(user);
    }

    public LoginResponse signup(String name, String email, String rawPassword, Long role, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .names(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .firstName(firstName)
                .lastName(lastName)
                .build();

        User savedUser = userRepository.save(user);
        return buildLoginResponseAndUpdateLastLogin(savedUser);
    }

    private LoginResponse buildLoginResponseAndUpdateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        List<Long> roleIds = user.getUserRoles().stream()
                .filter(userRole -> Boolean.TRUE.equals(userRole.getIsActive()))
                .map(userRole -> userRole.getRole().getId())
                .collect(Collectors.toList());

        List<String> roles = user.getUserRoles().stream()
                .filter(userRole -> Boolean.TRUE.equals(userRole.getIsActive()))
                .map(userRole -> userRole.getRole().getRoleCode())
                .collect(Collectors.toList());

        List<String> permissions = new ArrayList<>();
        if (!roleIds.isEmpty()) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIds(roleIds);
            permissions = rolePermissions.stream()
                    .flatMap(rp -> {
                        List<String> perms = new ArrayList<>();
                        String featureCode = rp.getFeature().getFeatureCode();
                        if (Boolean.TRUE.equals(rp.getCanRead())) {
                            perms.add(featureCode + ":READ");
                        }
                        if (Boolean.TRUE.equals(rp.getCanEdit())) {
                            perms.add(featureCode + ":EDIT");
                        }
                        if (Boolean.TRUE.equals(rp.getCanDelete())) {
                            perms.add(featureCode + ":DELETE");
                        }
                        return perms.stream();
                    })
                    .distinct()
                    .collect(Collectors.toList());
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("name", user.getNames());
        claims.put("email", user.getEmail());
        claims.put("avatar", user.getAvatar());
        claims.put("roles", roles);
        claims.put("permissions", permissions);

        String token = jwtUtil.generateToken(user.getEmail(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), claims);

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken) // TODO: Generate actual refresh token
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getUserId())
                        .name(user.getNames())
                        .email(user.getEmail())
                        .role(roles.isEmpty() ? "USER" : roles.get(0))
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }
}
