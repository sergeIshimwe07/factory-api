package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.CreateProductionRequest;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.entity.BillOfMaterials;
import com.factory.factory_erp.entity.ProductionEntry;
import com.factory.factory_erp.entity.enums.ProductionStatus;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.BillOfMaterialsRepository;
import com.factory.factory_erp.repository.ProductionEntryRepository;
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
public class ProductionService {
    
    private final ProductionEntryRepository productionEntryRepository;
    private final BillOfMaterialsRepository billOfMaterialsRepository;
    
    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getAllProductions(int page, int limit, String status) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<ProductionEntry> productionPage;
        
        if (status != null && !status.isEmpty()) {
            productionPage = productionEntryRepository.findByStatus(ProductionStatus.valueOf(status), pageable);
        } else {
            productionPage = productionEntryRepository.findAll(pageable);
        }
        
        List<Map<String, Object>> productions = productionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(productions, page, limit, productionPage.getTotalElements());
    }
    
    @Transactional
    public Map<String, Object> createProduction(CreateProductionRequest request) {
        BillOfMaterials bom = billOfMaterialsRepository.findByBomId(request.getBillOfMaterialsId())
                .orElseThrow(() -> new ResourceNotFoundException("BOM", "id", request.getBillOfMaterialsId()));
        
        ProductionEntry production = ProductionEntry.builder()
                .billOfMaterials(bom)
                .quantityProduced(request.getQuantityProduced())
                .quantityDefective(request.getQuantityDefective() != null ? request.getQuantityDefective() : 0)
                .supervisor(request.getSupervisor())
                .notes(request.getNotes())
                .build();
        
        ProductionEntry saved = productionEntryRepository.save(production);
        return mapToResponse(saved);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getAllBOMs(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<BillOfMaterials> bomPage = billOfMaterialsRepository.findAll(pageable);
        
        List<Map<String, Object>> boms = bomPage.getContent().stream()
                .map(this::mapBOMToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(boms, page, limit, bomPage.getTotalElements());
    }
    
    private Map<String, Object> mapToResponse(ProductionEntry production) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", production.getProductionId());
        response.put("batchNumber", production.getBatchNumber());
        response.put("billOfMaterialsId", production.getBillOfMaterials().getBomId());
        response.put("bomName", production.getBillOfMaterials().getFinishedProduct().getName());
        response.put("startDate", production.getStartDate());
        response.put("endDate", production.getEndDate());
        response.put("quantityProduced", production.getQuantityProduced());
        response.put("quantityDefective", production.getQuantityDefective());
        response.put("status", production.getStatus().name());
        response.put("supervisor", production.getSupervisor());
        response.put("notes", production.getNotes());
        return response;
    }
    
    private Map<String, Object> mapBOMToResponse(BillOfMaterials bom) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", bom.getBomId());
        response.put("finishedProductId", bom.getFinishedProduct().getProductId());
        response.put("finishedProductName", bom.getFinishedProduct().getName());
        response.put("status", bom.getStatus().name());
        response.put("createdAt", bom.getCreatedAt());
        
        List<Map<String, Object>> items = bom.getItems().stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", "bom_item_" + item.getId());
                    itemMap.put("rawMaterialId", item.getRawMaterial().getProductId());
                    itemMap.put("rawMaterialName", item.getRawMaterial().getName());
                    itemMap.put("quantityRequired", item.getQuantityRequired());
                    itemMap.put("unit", item.getUnit());
                    return itemMap;
                })
                .collect(Collectors.toList());
        response.put("items", items);
        
        return response;
    }
}
