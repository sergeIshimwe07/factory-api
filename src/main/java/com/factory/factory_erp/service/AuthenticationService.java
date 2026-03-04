package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.AuthResponse;
import com.factory.factory_erp.dto.LoginRequest;
import com.factory.factory_erp.dto.SignupRequest;
import com.factory.factory_erp.exception.AuthenticationException;
import com.factory.factory_erp.model.Employee;
import com.factory.factory_erp.repository.EmployeeRepository;
import com.factory.factory_erp.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Login user
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // Find employee by email
        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Employee not found"));
        
        // Check if employee can login
        if (!employee.getCanLogin()) {
            throw new AuthenticationException("Employee is not allowed to login");
        }
        
        // Validate password
        if (employee.getPassword() == null || !passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        // Update last login
        employee.setLastLogin(LocalDateTime.now());
        employeeRepository.save(employee);
        
        // Generate token
        String token = jwtTokenProvider.generateToken(employee);
        
        // Return response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .names(employee.getNames())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .employeeType(employee.getEmployeeType())
                .salary(employee.getSalary())
                .lastLogin(employee.getLastLogin())
                .status(employee.getStatus())
                .canLogin(employee.getCanLogin())
                .build();
    }
    
    /**
     * Signup user
     */
    public AuthResponse signup(SignupRequest signupRequest) {
        // Check if employee already exists by email
        if (employeeRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new AuthenticationException("Employee with this email already exists");
        }
        
        // Create new employee
        Employee employee = Employee.builder()
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .names(signupRequest.getNames())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .position(signupRequest.getPosition() != null ? signupRequest.getPosition() : 0)
                .employeeType(signupRequest.getEmployeeType())
                .salary(signupRequest.getSalary())
                .lastLogin(LocalDateTime.now())
                .status(1)
                .canLogin(true)
                .build();
        
        // Save employee
        employee = employeeRepository.save(employee);
        
        // Generate token
        String token = jwtTokenProvider.generateToken(employee);
        
        // Return response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .names(employee.getNames())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .employeeType(employee.getEmployeeType())
                .salary(employee.getSalary())
                .lastLogin(employee.getLastLogin())
                .status(employee.getStatus())
                .canLogin(employee.getCanLogin())
                .build();
    }
}
