package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateSupplierOrderRequest;
import com.factory.factory_erp.dto.request.SupplierOrderItemRequest;
import com.factory.factory_erp.dto.request.UpdateSupplierOrderStatusRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.dto.response.SupplierOrderItemResponse;
import com.factory.factory_erp.dto.response.SupplierOrderResponse;
import com.factory.factory_erp.entity.Product;
import com.factory.factory_erp.entity.Supplier;
import com.factory.factory_erp.entity.SupplierOrder;
import com.factory.factory_erp.entity.SupplierOrderItem;
import com.factory.factory_erp.entity.enums.SupplierOrderStatus;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.ProductRepository;
import com.factory.factory_erp.repository.SupplierOrderItemRepository;
import com.factory.factory_erp.repository.SupplierOrderRepository;
import com.factory.factory_erp.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierOrderService {
    
    private final SupplierOrderRepository supplierOrderRepository;
    private final SupplierOrderItemRepository supplierOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final InventoryService inventoryService;
    
    private static final String UPLOAD_DIR = "uploads";
    
    /**
     * Create a new supplier order
     */
    @Transactional
    public SupplierOrderResponse createSupplierOrder(CreateSupplierOrderRequest request) {
        log.info("Creating new supplier order for supplier: {}", request.getSupplierId());
        
        // Get supplier
        Supplier supplier = supplierRepository.findBySupplierId(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));
        
        // Create supplier order
        SupplierOrder order = SupplierOrder.builder()
                .supplier(supplier)
                .deliveryDate(request.getDeliveryDate())
                .notes(request.getNotes())
                .status(SupplierOrderStatus.draft)
                .build();
        
        // Add items to order
        for (SupplierOrderItemRequest itemRequest : request.getItems()) {
            Product rawMaterial = productRepository.findByProductId(itemRequest.getRawMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getRawMaterialId()));
            
            SupplierOrderItem item = SupplierOrderItem.builder()
                    .rawMaterial(rawMaterial)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .build();
            
            order.addItem(item);
        }
        
        // Save order
        SupplierOrder savedOrder = supplierOrderRepository.save(order);
        log.info("Supplier order created successfully: {}", savedOrder.getSupplierOrderId());
        
        // Convert to response
        SupplierOrderResponse response = mapToResponse(savedOrder);
        
        // Send email notification to supplier
        try {
            emailService.sendSupplierOrderNotification(response);
        } catch (Exception e) {
            log.error("Failed to send email notification, but order was created", e);
        }
        
        return response;
    }
    
    /**
     * Get all supplier orders with filtering
     */
    @Transactional(readOnly = true)
    public PageResponse<SupplierOrderResponse> getAllSupplierOrders(
            int page, int limit, String status, String supplierId, 
            LocalDate startDate, LocalDate endDate, String search) {
        
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        SupplierOrderStatus orderStatus = null;
        Long supplierIdLong = null;
        
        if (status != null && !status.isEmpty()) {
            try {
                orderStatus = SupplierOrderStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
            }
        }
        
        if (supplierId != null && !supplierId.isEmpty()) {
            Supplier supplier = supplierRepository.findBySupplierId(supplierId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));
            supplierIdLong = supplier.getId();
        }
        
        Page<SupplierOrder> orders = supplierOrderRepository.findWithFilters(
                orderStatus, supplierIdLong, startDate, endDate, search, pageable
        );
        
        List<SupplierOrderResponse> responses = orders.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(responses, page, limit, orders.getTotalElements());
    }
    
    /**
     * Get supplier order by ID
     */
    @Transactional(readOnly = true)
    public SupplierOrderResponse getSupplierOrderById(String supplierOrderId) {
        SupplierOrder order = supplierOrderRepository.findBySupplierOrderId(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier Order", "id", supplierOrderId));
        
        return mapToResponse(order);
    }
    
    /**
     * Update supplier order status
     */
    @Transactional
    public SupplierOrderResponse updateSupplierOrderStatus(String supplierOrderId, UpdateSupplierOrderStatusRequest request) {
        SupplierOrder order = supplierOrderRepository.findBySupplierOrderId(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier Order", "id", supplierOrderId));
        
        SupplierOrderStatus newStatus;
        try {
            newStatus = SupplierOrderStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.getStatus());
        }
        
        // Validate status transition
        validateStatusTransition(order.getStatus(), newStatus);
        
        // Update status
        order.setStatus(newStatus);
        
        // If status is "received", add items to inventory
        if (newStatus == SupplierOrderStatus.received) {
            addOrderedItemsToInventory(order);
        }
        
        SupplierOrder updated = supplierOrderRepository.save(order);
        log.info("Supplier order {} status updated to {}", supplierOrderId, newStatus);
        
        return mapToResponse(updated);
    }
    
    /**
     * Add ordered items to inventory (store)
     */
    private void addOrderedItemsToInventory(SupplierOrder order) {
        log.info("Adding ordered items to inventory for order: {}", order.getSupplierOrderId());
        
        for (SupplierOrderItem item : order.getItems()) {
            try {
                Product product = item.getRawMaterial();
                
                // Update current stock
                Integer newStock = product.getCurrentStock() + item.getQuantity();
                product.setCurrentStock(newStock);
                
                // Save updated product
                productRepository.save(product);
                
                log.info("Added {} units of {} to inventory", item.getQuantity(), product.getProductId());
            } catch (Exception e) {
                log.error("Failed to add item to inventory", e);
                throw new RuntimeException("Failed to update inventory for item: " + item.getRawMaterial().getName(), e);
            }
        }
    }
    
    /**
     * Validate status transition
     */
    private void validateStatusTransition(SupplierOrderStatus currentStatus, SupplierOrderStatus newStatus) {
        List<SupplierOrderStatus> validTransitions = new ArrayList<>();
        
        switch (currentStatus) {
            case draft:
                validTransitions.add(SupplierOrderStatus.sent);
                validTransitions.add(SupplierOrderStatus.cancelled);
                break;
            case sent:
                validTransitions.add(SupplierOrderStatus.confirmed);
                validTransitions.add(SupplierOrderStatus.cancelled);
                break;
            case confirmed:
                validTransitions.add(SupplierOrderStatus.partial_received);
                validTransitions.add(SupplierOrderStatus.cancelled);
                break;
            case partial_received:
                validTransitions.add(SupplierOrderStatus.received);
                validTransitions.add(SupplierOrderStatus.cancelled);
                break;
            case received:
                validTransitions.add(SupplierOrderStatus.invoiced);
                validTransitions.add(SupplierOrderStatus.cancelled);
                break;
            case invoiced:
                validTransitions.add(SupplierOrderStatus.paid);
                validTransitions.add(SupplierOrderStatus.cancelled);
                break;
            case paid:
            case cancelled:
                // Terminal states
                break;
            default:
                throw new IllegalArgumentException("Unknown status: " + currentStatus);
        }
        
        if (!validTransitions.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus +
                    ". Valid transitions: " + validTransitions
            );
        }
    }
    
    /**
     * Delete supplier order
     */
    @Transactional
    public void deleteSupplierOrder(String supplierOrderId) {
        SupplierOrder order = supplierOrderRepository.findBySupplierOrderId(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier Order", "id", supplierOrderId));
        
        // Only draft orders can be deleted
        if (order.getStatus() != SupplierOrderStatus.draft) {
            throw new IllegalArgumentException(
                    "Cannot delete order with status '" + order.getStatus() + 
                    "'. Only draft orders can be deleted."
            );
        }
        
        supplierOrderRepository.delete(order);
        log.info("Supplier order deleted: {}", supplierOrderId);
    }
    
    /**
     * Upload proof of payment
     */
    @Transactional
    public SupplierOrderResponse uploadProofOfPayment(String supplierOrderId, MultipartFile file) {
        SupplierOrder order = supplierOrderRepository.findBySupplierOrderId(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier Order", "id", supplierOrderId));
        
        try {
            String fileName = saveFile(file, "proof-of-payment");
            order.setProofOfPayment(fileName);
            
            SupplierOrder updated = supplierOrderRepository.save(order);
            log.info("Proof of payment uploaded for order: {}", supplierOrderId);
            
            return mapToResponse(updated);
        } catch (IOException e) {
            log.error("Failed to upload proof of payment", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    /**
     * Upload invoice
     */
    @Transactional
    public SupplierOrderResponse uploadInvoice(String supplierOrderId, MultipartFile file) {
        SupplierOrder order = supplierOrderRepository.findBySupplierOrderId(supplierOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier Order", "id", supplierOrderId));
        
        try {
            String fileName = saveFile(file, "invoices");
            order.setInvoice(fileName);
            
            SupplierOrder updated = supplierOrderRepository.save(order);
            log.info("Invoice uploaded for order: {}", supplierOrderId);
            
            return mapToResponse(updated);
        } catch (IOException e) {
            log.error("Failed to upload invoice", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    /**
     * Save file to disk
     */
    private String saveFile(MultipartFile file, String directory) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR, directory);
        
        Files.createDirectories(uploadPath);
        Files.write(uploadPath.resolve(fileName), file.getBytes());
        
        return fileName;
    }
    
    /**
     * Map SupplierOrder entity to SupplierOrderResponse DTO
     */
    private SupplierOrderResponse mapToResponse(SupplierOrder order) {
        List<SupplierOrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> SupplierOrderItemResponse.builder()
                        .id(item.getId())
                        .rawMaterialId(item.getRawMaterial().getProductId())
                        .rawMaterialName(item.getRawMaterial().getName())
                        .quantity(item.getQuantity())
                        .unit(item.getRawMaterial().getUnit().name())
                        .unitPrice(item.getUnitPrice())
                        .total(item.getTotal())
                        .build())
                .collect(Collectors.toList());
        
        return SupplierOrderResponse.builder()
                .id(order.getSupplierOrderId())
                .supplierId(order.getSupplier().getSupplierId())
                .supplierName(order.getSupplier().getName())
                .supplierEmail(order.getSupplier().getEmail())
                .supplierPhone(order.getSupplier().getPhone())
                .deliveryDate(order.getDeliveryDate())
                .notes(order.getNotes())
                .status(order.getStatus().name())
                .items(itemResponses)
                .itemCount(order.getItems().size())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .total(order.getTotal())
                .proofOfPayment(order.getProofOfPayment())
                .invoice(order.getInvoice())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
