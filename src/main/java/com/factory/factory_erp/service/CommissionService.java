package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateCommissionRuleRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.entity.Commission;
import com.factory.factory_erp.entity.CommissionRule;
import com.factory.factory_erp.entity.Product;
import com.factory.factory_erp.entity.enums.CommissionStatus;
import com.factory.factory_erp.entity.enums.CommissionType;
import com.factory.factory_erp.entity.enums.Status;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.CommissionRepository;
import com.factory.factory_erp.repository.CommissionRuleRepository;
import com.factory.factory_erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionService {
    
    private final CommissionRepository commissionRepository;
    private final CommissionRuleRepository commissionRuleRepository;
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getAllCommissions(int page, int limit, String status) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Commission> commissionPage;
        
        if (status != null && !status.isEmpty()) {
            commissionPage = commissionRepository.findByStatus(CommissionStatus.valueOf(status), pageable);
        } else {
            commissionPage = commissionRepository.findAll(pageable);
        }
        
        List<Map<String, Object>> commissions = commissionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(commissions, page, limit, commissionPage.getTotalElements());
    }
    
    @Transactional
    public Map<String, Object> markCommissionAsPaid(String commissionId, Map<String, String> request) {
        Commission commission = commissionRepository.findByCommissionId(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission", "id", commissionId));
        
        commission.setStatus(CommissionStatus.paid);
        commission.setReference(request.get("reference"));
        commission.setPaidDate(LocalDate.parse(request.get("paidDate")));
        
        Commission updated = commissionRepository.save(commission);
        return mapToResponse(updated);
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllCommissionRules() {
        List<CommissionRule> rules = commissionRuleRepository.findByStatus(Status.active);
        return rules.stream()
                .map(this::mapRuleToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Map<String, Object> createCommissionRule(CreateCommissionRuleRequest request) {
        Product product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        
        CommissionRule rule = CommissionRule.builder()
                .product(product)
                .type(CommissionType.valueOf(request.getType()))
                .value(request.getValue())
                .build();
        
        CommissionRule saved = commissionRuleRepository.save(rule);
        return mapRuleToResponse(saved);
    }
    
    @Transactional
    public Map<String, Object> updateCommissionRule(String ruleId, Map<String, String> updates) {
        CommissionRule rule = commissionRuleRepository.findByRuleId(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission Rule", "id", ruleId));
        
        if (updates.containsKey("value")) {
            rule.setValue(new java.math.BigDecimal(updates.get("value")));
        }
        
        CommissionRule updated = commissionRuleRepository.save(rule);
        return mapRuleToResponse(updated);
    }
    
    @Transactional
    public void deleteCommissionRule(String ruleId) {
        CommissionRule rule = commissionRuleRepository.findByRuleId(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission Rule", "id", ruleId));
        
        rule.setStatus(Status.inactive);
        commissionRuleRepository.save(rule);
    }
    
    private Map<String, Object> mapToResponse(Commission commission) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", commission.getCommissionId());
        response.put("agentId", commission.getAgent().getUserId());
        response.put("agentName", commission.getAgent().getName());
        response.put("period", commission.getPeriod());
        response.put("saleAmount", commission.getSaleAmount());
        response.put("rate", commission.getRate());
        response.put("commission", commission.getCommission());
        response.put("status", commission.getStatus().name());
        response.put("notes", commission.getNotes());
        return response;
    }
    
    private Map<String, Object> mapRuleToResponse(CommissionRule rule) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", rule.getRuleId());
        response.put("productId", rule.getProduct().getProductId());
        response.put("productName", rule.getProduct().getName());
        response.put("type", rule.getType().name());
        response.put("value", rule.getValue());
        response.put("status", rule.getStatus().name());
        return response;
    }
}
