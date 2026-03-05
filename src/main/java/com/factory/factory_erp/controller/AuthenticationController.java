package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.LoginRequest;
import com.factory.factory_erp.dto.LoginResponse;
import com.factory.factory_erp.dto.SignupRequest;
import com.factory.factory_erp.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    /**
     * Login endpoint
     * POST /api/auth/login
     * Request body: { "email": "user@example.com", "password": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Signup endpoint
     * POST /api/auth/signup
     * Request body: { "firstName": "...", "lastName": "...", "names": "...", "email": "user@example.com",
     *                 "password": "...", "position": 0, "employeeType": "...", "salary": 0.0 }
     */
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        LoginResponse response = authenticationService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
