package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateSupplierRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.entity.Supplier;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getAllSuppliers(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Supplier> supplierPage = supplierRepository.findAll(pageable);
        
        List<Map<String, Object>> suppliers = supplierPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(suppliers, page, limit, supplierPage.getTotalElements());
    }
    
    @Transactional
    public Map<String, Object> createSupplier(CreateSupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .contactPerson(request.getContactPerson())
                .build();
        
        Supplier saved = supplierRepository.save(supplier);
        return mapToResponse(saved);
    }
    
    @Transactional
    public Map<String, Object> updateSupplier(String supplierId, Map<String, String> updates) {
        Supplier supplier = supplierRepository.findBySupplierId(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));
        
        if (updates.containsKey("phone")) supplier.setPhone(updates.get("phone"));
        if (updates.containsKey("email")) supplier.setEmail(updates.get("email"));
        if (updates.containsKey("address")) supplier.setAddress(updates.get("address"));
        if (updates.containsKey("contactPerson")) supplier.setContactPerson(updates.get("contactPerson"));
        
        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }
    
    private Map<String, Object> mapToResponse(Supplier supplier) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", supplier.getSupplierId());
        response.put("name", supplier.getName());
        response.put("email", supplier.getEmail());
        response.put("phone", supplier.getPhone());
        response.put("address", supplier.getAddress());
        response.put("contactPerson", supplier.getContactPerson());
        response.put("status", supplier.getStatus().name());
        response.put("totalPurchases", supplier.getTotalPurchases());
        response.put("outstandingBalance", supplier.getOutstandingBalance());
        return response;
    }
}
