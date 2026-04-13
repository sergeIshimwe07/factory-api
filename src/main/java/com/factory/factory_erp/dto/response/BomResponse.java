package com.factory.factory_erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomResponse {
    
    private String id;
    private String finishedProductId;
    private String finishedProductName;
    private List<BomItemResponse> items;
    private String status;
    private LocalDateTime createdAt;
}
