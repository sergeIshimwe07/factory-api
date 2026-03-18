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
public class PageResponse<T> {
    
    private List<T> data;
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
        private boolean hasMore;
    }
    
    public static <T> PageResponse<T> of(List<T> data, int page, int limit, long total) {
        int totalPages = (int) Math.ceil((double) total / limit);
        
        return PageResponse.<T>builder()
                .data(data)
                .pagination(PaginationInfo.builder()
                        .page(page)
                        .limit(limit)
                        .total(total)
                        .pages(totalPages)
                        .hasMore(page < totalPages)
                        .build())
                .build();
    }
}
