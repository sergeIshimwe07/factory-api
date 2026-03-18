package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateStockMovementRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.entity.Product;
import com.factory.factory_erp.entity.StockMovement;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.entity.enums.MovementType;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.ProductRepository;
import com.factory.factory_erp.repository.StockMovementRepository;
import com.factory.factory_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getStockMovements(int page, int limit, String type, String dateFrom) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<StockMovement> movementPage;
        
        if (type != null && !type.isEmpty() && dateFrom != null && !dateFrom.isEmpty()) {
            LocalDateTime startDate = LocalDateTime.parse(dateFrom + "T00:00:00");
            movementPage = stockMovementRepository.findByTypeAndDateAfter(
                MovementType.valueOf(type), startDate, pageable);
        } else if (type != null && !type.isEmpty()) {
            movementPage = stockMovementRepository.findByType(MovementType.valueOf(type), pageable);
        } else {
            movementPage = stockMovementRepository.findAll(pageable);
        }
        
        List<Map<String, Object>> movements = movementPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(movements, page, limit, movementPage.getTotalElements());
    }
    
    @Transactional
    public Map<String, Object> createStockMovement(CreateStockMovementRequest request) {
        Product product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        
        User currentUser = getCurrentUser();
        MovementType type = MovementType.valueOf(request.getType());
        
        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(type)
                .quantity(request.getQuantity())
                .reference(request.getReference())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();
        
        StockMovement saved = stockMovementRepository.save(movement);
        
        int currentStock = product.getCurrentStock();
        if (type == MovementType.receipt || type == MovementType.return_stock) {
            product.setCurrentStock(currentStock + request.getQuantity());
        } else if (type == MovementType.issue || type == MovementType.adjustment) {
            product.setCurrentStock(currentStock - request.getQuantity());
        }
        productRepository.save(product);
        
        return mapToResponse(saved);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
    
    private Map<String, Object> mapToResponse(StockMovement movement) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", movement.getMovementId());
        response.put("productId", movement.getProduct().getProductId());
        response.put("productName", movement.getProduct().getName());
        response.put("type", movement.getType().name());
        response.put("quantity", movement.getQuantity());
        response.put("date", movement.getCreatedAt());
        response.put("reference", movement.getReference());
        response.put("notes", movement.getNotes());
        response.put("createdBy", movement.getCreatedBy() != null ? movement.getCreatedBy().getName() : null);
        return response;
    }
}
