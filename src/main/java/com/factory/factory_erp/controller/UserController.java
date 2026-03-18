package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateUserRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        PageResponse<Map<String, Object>> response = userService.getAllUsers(page, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createUser(@Valid @RequestBody CreateUserRequest request) {
        Map<String, Object> response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User created successfully"));
    }
    
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleUserActive(@PathVariable String id) {
        Map<String, Object> response = userService.toggleUserActive(id);
        return ResponseEntity.ok(ApiResponse.success(response, "User status updated successfully"));
    }
    
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        userService.resetPassword(id, request.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }
}
