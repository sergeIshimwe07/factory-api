package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateCustomerRequest;
import com.factory.factory_erp.dto.request.UpdateCustomerRequest;
import com.factory.factory_erp.dto.response.CustomerResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.entity.Customer;
import com.factory.factory_erp.entity.enums.CustomerType;
import com.factory.factory_erp.entity.enums.Status;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> getAllCustomers(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        
        List<CustomerResponse> customers = customerPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(customers, page, limit, customerPage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        return mapToResponse(customer);
    }
    
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.getName())
                .type(CustomerType.valueOf(request.getType()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.ZERO)
                .build();
        
        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }
    
    @Transactional
    public CustomerResponse updateCustomer(String customerId, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getType() != null) customer.setType(CustomerType.valueOf(request.getType()));
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getCity() != null) customer.setCity(request.getCity());
        if (request.getCreditLimit() != null) customer.setCreditLimit(request.getCreditLimit());
        
        Customer updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }
    
    @Transactional
    public CustomerResponse toggleBlockCustomer(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        customer.setIsBlocked(!customer.getIsBlocked());
        Customer updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> searchCustomers(String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Customer> customerPage = customerRepository.searchCustomers(search, pageable);
        
        List<CustomerResponse> customers = customerPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(customers, page, limit, customerPage.getTotalElements());
    }
    
    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getCustomerId())
                .name(customer.getName())
                .type(customer.getType().name())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .creditLimit(customer.getCreditLimit())
                .outstandingCredit(customer.getOutstandingCredit())
                .totalSales(customer.getTotalSales())
                .totalOrders(customer.getTotalOrders())
                .lastOrderDate(customer.getLastOrderDate())
                .isBlocked(customer.getIsBlocked())
                .status(customer.getStatus().name())
                .createdDate(customer.getCreatedAt())
                .build();
    }
}
