package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateSalesOrderRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.dto.response.SalesOrderResponse;
import com.factory.factory_erp.entity.*;
import com.factory.factory_erp.entity.enums.OrderStatus;
import com.factory.factory_erp.entity.enums.PaymentStatus;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.CustomerRepository;
import com.factory.factory_erp.repository.ProductRepository;
import com.factory.factory_erp.repository.SalesOrderRepository;
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
public class SalesOrderService {
    
    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<SalesOrderResponse> getAllSalesOrders(int page, int limit, String status) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<SalesOrder> orderPage;
        
        if (status != null && !status.isEmpty()) {
            orderPage = salesOrderRepository.findByStatus(OrderStatus.valueOf(status), pageable);
        } else {
            orderPage = salesOrderRepository.findAll(pageable);
        }
        
        List<SalesOrderResponse> orders = orderPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(orders, page, limit, orderPage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public SalesOrderResponse getSalesOrderById(String saleId) {
        SalesOrder order = salesOrderRepository.findBySaleId(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales Order", "id", saleId));
        return mapToResponse(order);
    }
    
    @Transactional
    public SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request) {
        Customer customer = customerRepository.findByCustomerId(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        
        SalesOrder order = SalesOrder.builder()
                .customer(customer)
                .dueDate(request.getDueDate())
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();
        
        for (CreateSalesOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findByProductId(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));
            
            SalesOrderItem item = SalesOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .discount(itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO)
                    .build();
            
            order.addItem(item);
        }
        
        order.calculateTotals();
        
        SalesOrder saved = salesOrderRepository.save(order);
        
        customer.setTotalOrders(customer.getTotalOrders() + 1);
        customer.setTotalSales(customer.getTotalSales().add(saved.getTotal()));
        customer.setLastOrderDate(saved.getOrderDate());
        customerRepository.save(customer);
        
        return mapToResponse(saved);
    }
    
    @Transactional
    public SalesOrderResponse updateOrderStatus(String saleId, String status) {
        SalesOrder order = salesOrderRepository.findBySaleId(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales Order", "id", saleId));
        
        order.setStatus(OrderStatus.valueOf(status));
        SalesOrder updated = salesOrderRepository.save(order);
        return mapToResponse(updated);
    }
    
    private SalesOrderResponse mapToResponse(SalesOrder order) {
        List<SalesOrderResponse.OrderItemResponse> items = order.getItems().stream()
                .map(item -> SalesOrderResponse.OrderItemResponse.builder()
                        .productId(item.getProduct().getProductId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discount(item.getDiscount())
                        .total(item.getTotal())
                        .build())
                .collect(Collectors.toList());
        
        return SalesOrderResponse.builder()
                .id(order.getSaleId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .customerPhone(order.getCustomer().getPhone())
                .date(order.getOrderDate())
                .dueDate(order.getDueDate())
                .items(items)
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .agent(order.getAgent() != null ? order.getAgent().getName() : null)
                .notes(order.getNotes())
                .build();
    }
}
