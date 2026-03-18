package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.LoginRequest;
import com.factory.factory_erp.dto.response.LoginResponse;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.repository.UserRepository;
import com.factory.factory_erp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        java.util.List<String> roles = user.getUserRoles().stream()
                .filter(userRole -> userRole.getIsActive())
                .map(userRole -> userRole.getRole().getRoleCode())
                .collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roles", roles);
        
        String token = jwtUtil.generateToken(user.getEmail(), claims);
        
        return LoginResponse.builder()
                .token(token)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getUserId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(roles.isEmpty() ? "USER" : roles.get(0))
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }
}
