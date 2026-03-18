package com.factory.factory_erp.controller;

import com.factory.factory_erp.dto.request.CreateCustomerRequest;
import com.factory.factory_erp.dto.request.UpdateCustomerRequest;
import com.factory.factory_erp.dto.response.ApiResponse;
import com.factory.factory_erp.dto.response.CustomerResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        PageResponse<CustomerResponse> response = customerService.getAllCustomers(page, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable String id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Customer created successfully"));
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable String id,
            @RequestBody UpdateCustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer updated successfully"));
    }
    
    @PatchMapping("/{id}/toggle-block")
    public ResponseEntity<ApiResponse<CustomerResponse>> toggleBlockCustomer(@PathVariable String id) {
        CustomerResponse response = customerService.toggleBlockCustomer(id);
        String message = response.getIsBlocked() ? "Customer blocked successfully" : "Customer unblocked successfully";
        return ResponseEntity.ok(ApiResponse.success(response, message));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> searchCustomers(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        PageResponse<CustomerResponse> response = customerService.searchCustomers(q, page, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
