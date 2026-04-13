package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.request.BomItemRequest;
import com.factory.factory_erp.dto.request.CreateBOMRequest;
import com.factory.factory_erp.dto.request.CreateProductionRequest;
import com.factory.factory_erp.dto.response.BomItemResponse;
import com.factory.factory_erp.dto.response.BomResponse;
import com.factory.factory_erp.dto.response.PaginatedBomResponse;
import com.factory.factory_erp.dto.response.PageResponse;
import com.factory.factory_erp.entity.BillOfMaterials;
import com.factory.factory_erp.entity.BomItem;
import com.factory.factory_erp.entity.Product;
import com.factory.factory_erp.entity.ProductionEntry;
import com.factory.factory_erp.entity.enums.ProductionStatus;
import com.factory.factory_erp.exception.ResourceNotFoundException;
import com.factory.factory_erp.repository.BillOfMaterialsRepository;
import com.factory.factory_erp.repository.ProductionEntryRepository;
import com.factory.factory_erp.repository.ProductRepository;
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
    private final ProductRepository productRepository;
    
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
    public PaginatedBomResponse getAllBOMs(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<BillOfMaterials> bomPage = billOfMaterialsRepository.findAll(pageable);
        
        List<BomResponse> boms = bomPage.getContent().stream()
                .map(this::mapToBomResponse)
                .collect(Collectors.toList());
        
        return PaginatedBomResponse.of(boms, page, limit, bomPage.getTotalElements());
    }
    
    @Transactional
    public Map<String, Object> createBOM(CreateBOMRequest request) {
        // Get the finished product
        Product finishedProduct = productRepository.findByProductId(request.getFinishedProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getFinishedProductId()));
        
        // Create new BOM
        BillOfMaterials bom = BillOfMaterials.builder()
                .finishedProduct(finishedProduct)
                .build();
        
        // Add items to BOM
        for (BomItemRequest itemRequest : request.getItems()) {
            Product rawMaterial = productRepository.findByProductId(itemRequest.getRawMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getRawMaterialId()));
            
            BomItem bomItem = BomItem.builder()
                    .rawMaterial(rawMaterial)
                    .quantityRequired(itemRequest.getQuantityRequired())
                    .unit(itemRequest.getUnit())
                    .build();
            
            bom.addItem(bomItem);
        }
        
        // Save BOM
        BillOfMaterials savedBom = billOfMaterialsRepository.save(bom);
        
        // Return response
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedBom.getBomId());
        response.put("finishedProductId", savedBom.getFinishedProduct().getProductId());
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public BomResponse getBOMByFinishedProductId(String finishedProductId) {
        // Get BOM by finished product's productId directly
        BillOfMaterials bom = billOfMaterialsRepository.findByFinishedProductProductId(finishedProductId)
                .orElseThrow(() -> new ResourceNotFoundException("BOM", "finishedProductId", finishedProductId));
        
        return mapToBomResponse(bom);
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
    
    private BomResponse mapToBomResponse(BillOfMaterials bom) {
        List<BomItemResponse> items = bom.getItems().stream()
                .map(item -> BomItemResponse.builder()
                        .id("bom_item_" + item.getId())
                        .rawMaterialId(item.getRawMaterial().getProductId())
                        .rawMaterialName(item.getRawMaterial().getName())
                        .quantityRequired(item.getQuantityRequired())
                        .unit(item.getUnit())
                        .build())
                .collect(Collectors.toList());
        
        return BomResponse.builder()
                .id(bom.getBomId())
                .finishedProductId(bom.getFinishedProduct().getProductId())
                .finishedProductName(bom.getFinishedProduct().getName())
                .items(items)
                .status(bom.getStatus().name())
                .createdAt(bom.getCreatedAt())
                .build();
    }
}
