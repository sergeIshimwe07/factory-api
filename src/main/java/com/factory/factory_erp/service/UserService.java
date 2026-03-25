package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateUserRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleManagementService roleManagementService;
    
    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getAllUsers(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> userPage = userRepository.findAll(pageable);
        
        List<Map<String, Object>> users = userPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(users, page, limit, userPage.getTotalElements());
    }
    
    @Transactional
    public Map<String, Object> createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
                .names(request.getNames())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        User saved = userRepository.save(user);
        
        if (request.getRole() != null) {
            roleManagementService.assignRoleToUser(saved.getId(), request.getRole(), null);
        }
        
        return mapToResponse(saved);
    }
    
    @Transactional
    public Map<String, Object> toggleUserActive(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setIsActive(!user.getIsActive());
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }
    
    @Transactional
    public void resetPassword(String userId, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    private Map<String, Object> mapToResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getUserId());
        response.put("name", user.getNames());
        response.put("email", user.getEmail());
        
        List<String> roles = user.getUserRoles().stream()
                .filter(ur -> ur.getIsActive())
                .map(ur -> ur.getRole().getRoleCode())
                .collect(Collectors.toList());
        
        response.put("roles", roles);
        response.put("role", roles.isEmpty() ? "USER" : roles.get(0));
        response.put("status", user.getIsActive() ? "active" : "inactive");
        response.put("lastLogin", user.getLastLogin());
        return response;
    }
}
