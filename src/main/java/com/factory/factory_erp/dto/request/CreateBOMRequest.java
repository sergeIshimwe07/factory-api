package com.factory.factory_erp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBOMRequest {
    
    @NotBlank(message = "Finished product ID is required")
    private String finishedProductId;
    
    @NotEmpty(message = "BOM must contain at least one item")
    @Valid
    private List<BomItemRequest> items;
}
