package com.arquisoft.shared.pagination;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class PaginatedResult<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean empty;

    public PaginatedResult(List<T> content, int page, int size, long totalElements) {
        this.content = Collections.unmodifiableList(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (size > 0 && totalElements > 0)
                ? (int) Math.ceil((double) totalElements / size)
                : 0;
        this.first = (page == 0);
        this.last = (this.totalPages == 0) || (page >= this.totalPages - 1);
        this.empty = content.isEmpty();
    }

    public static <T> PaginatedResult<T> of(List<T> content, int page, int size, long totalElements) {
        return new PaginatedResult<>(content, page, size, totalElements);
    }

    public <U> PaginatedResult<U> map(Function<T, U> mapper) {
        List<U> mappedContent = this.content.stream()
                .map(mapper)
                .toList();
        return new PaginatedResult<>(mappedContent, this.page, this.size, this.totalElements);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }
}
