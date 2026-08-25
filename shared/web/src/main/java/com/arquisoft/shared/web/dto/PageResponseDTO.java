package com.arquisoft.shared.web.dto;

import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponseDTO<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private Boolean first;
    private Boolean last;
    private Boolean empty;

    public static <T> PageResponseDTO<T> from(PaginatedResult<T> result) {
        return PageResponseDTO.<T>builder()
                .content(result.getContent())
                .page(result.getPage())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .empty(result.isEmpty())
                .build();
    }
}
