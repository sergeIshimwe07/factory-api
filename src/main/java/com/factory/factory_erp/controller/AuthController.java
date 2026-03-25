package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateUserRequest;
import com.factory.factory_erp.dto.request.LoginRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.LoginResponse;
import com.factory.factory_erp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(@Valid @RequestBody CreateUserRequest request) {
        LoginResponse response = authService.signup(request.getNames(), request.getEmail(), request.getPassword(), request.getRole(), request.getFirstName(), request.getLastName());
        return ResponseEntity.ok(ApiResponse.success(response, "Signup successful"));
    }
}
