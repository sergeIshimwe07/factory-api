package com.factory.factory_erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedBomResponse {
    
    private List<BomResponse> data;
    private PaginationInfo pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int pages;
    }
    
    public static PaginatedBomResponse of(List<BomResponse> data, int page, int limit, long total) {
        int totalPages = (int) Math.ceil((double) total / limit);
        
        return PaginatedBomResponse.builder()
                .data(data)
                .pagination(PaginationInfo.builder()
                        .page(page)
                        .limit(limit)
                        .total(total)
                        .pages(totalPages)
                        .build())
                .build();
    }
}
