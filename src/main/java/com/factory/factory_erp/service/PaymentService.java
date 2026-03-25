package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreatePaymentRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.dto.response.PaymentResponse;
import com.factory.factory_erp.entity.Payment;
import com.factory.factory_erp.entity.SalesOrder;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.entity.enums.PaymentMethod;
import com.factory.factory_erp.entity.enums.PaymentStatus;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.PaymentRepository;
import com.factory.factory_erp.repository.SalesOrderRepository;
import com.factory.factory_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getAllPayments(int page, int limit, String customerId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Payment> paymentPage;
        
        if (customerId != null && !customerId.isEmpty()) {
            paymentPage = paymentRepository.findAll(pageable);
        } else {
            paymentPage = paymentRepository.findAll(pageable);
        }
        
        List<PaymentResponse> payments = paymentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(payments, page, limit, paymentPage.getTotalElements());
    }
    
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return mapToResponse(payment);
    }
    
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        SalesOrder salesOrder = salesOrderRepository.findBySaleId(request.getSaleId())
                .orElseThrow(() -> new ResourceNotFoundException("Sales Order", "id", request.getSaleId()));
        
        User currentUser = getCurrentUser();
        
        Payment payment = Payment.builder()
                .salesOrder(salesOrder)
                .amount(request.getAmount())
                .method(PaymentMethod.valueOf(request.getMethod()))
                .reference(request.getReference())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();
        
        Payment saved = paymentRepository.save(payment);
        
        BigDecimal totalPaid = paymentRepository.getTotalPaidForOrder(salesOrder.getId());
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        
        if (totalPaid.compareTo(salesOrder.getTotal()) >= 0) {
            salesOrder.setPaymentStatus(PaymentStatus.paid);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            salesOrder.setPaymentStatus(PaymentStatus.partial);
        }
        salesOrderRepository.save(salesOrder);
        
        return mapToResponse(saved);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
    
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getPaymentId())
                .saleId(payment.getSalesOrder().getSaleId())
                .saleInvoice(payment.getSalesOrder().getOrderNumber())
                .customerId(payment.getSalesOrder().getCustomer().getCustomerId())
                .customerName(payment.getSalesOrder().getCustomer().getName())
                .amount(payment.getAmount())
                .method(payment.getMethod().name())
                .reference(payment.getReference())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .createdBy(payment.getCreatedBy() != null ? payment.getCreatedBy().getNames() : null)
                .build();
    }
}
