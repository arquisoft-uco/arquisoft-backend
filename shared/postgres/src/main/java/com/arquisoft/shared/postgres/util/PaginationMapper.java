package com.arquisoft.shared.postgres.util;

import com.arquisoft.shared.pagination.PaginatedResult;
import org.springframework.data.domain.Page;

public final class PaginationMapper {

    private PaginationMapper() {}

    public static <T> PaginatedResult<T> toResult(Page<T> page) {
        return PaginatedResult.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }
}
