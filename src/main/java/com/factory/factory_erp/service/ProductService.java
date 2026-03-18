package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateProductRequest;
import com.factory.factory_erp.dto.request.UpdateProductRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.dto.response.ProductResponse;
import com.factory.factory_erp.entity.Product;
import com.factory.factory_erp.entity.enums.ProductStatus;
import com.factory.factory_erp.entity.enums.ProductUnit;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(int page, int limit, String category, String status) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Product> productPage;
        
        if (category != null && !category.isEmpty()) {
            productPage = productRepository.findByCategory(category, pageable);
        } else if (status != null && !status.isEmpty()) {
            productPage = productRepository.findByStatus(ProductStatus.valueOf(status), pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        List<ProductResponse> products = productPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(products, page, limit, productPage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return mapToResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .unit(ProductUnit.valueOf(request.getUnit()))
                .basePrice(request.getBasePrice())
                .costPrice(request.getCostPrice())
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 0)
                .reorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 0)
                .weight(request.getWeight())
                .description(request.getDescription())
                .build();
        
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }
    
    @Transactional
    public ProductResponse updateProduct(String productId, UpdateProductRequest request) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getUnit() != null) product.setUnit(ProductUnit.valueOf(request.getUnit()));
        if (request.getBasePrice() != null) product.setBasePrice(request.getBasePrice());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getMinimumStock() != null) product.setMinimumStock(request.getMinimumStock());
        if (request.getReorderLevel() != null) product.setReorderLevel(request.getReorderLevel());
        if (request.getWeight() != null) product.setWeight(request.getWeight());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getStatus() != null) product.setStatus(ProductStatus.valueOf(request.getStatus()));
        
        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Product> productPage = productRepository.searchProducts(search, pageable);
        
        List<ProductResponse> products = productPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(products, page, limit, productPage.getTotalElements());
    }
    
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getProductId())
                .sku(product.getSku())
                .name(product.getName())
                .category(product.getCategory())
                .unit(product.getUnit().name())
                .basePrice(product.getBasePrice())
                .costPrice(product.getCostPrice())
                .currentStock(product.getCurrentStock())
                .minimumStock(product.getMinimumStock())
                .reorderLevel(product.getReorderLevel())
                .weight(product.getWeight())
                .description(product.getDescription())
                .status(product.getStatus().name())
                .build();
    }
}
