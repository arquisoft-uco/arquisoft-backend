package com.arquisoft.shared.domain;

import java.util.List;

/**
 * Domain representation of a paginated result.
 * Pure Java: no Spring, no Jackson, no Lombok, no framework dependencies.
 *
 * <p>This type is the canonical pagination contract for use cases (input ports).
 * Infrastructure adapters are responsible for mapping it to transport-specific
 * representations (e.g. {@code PageResponseDTO} for HTTP, message payloads for AMQP).
 *
 * <p>Convention follows Spring Data semantics for familiarity:
 * content, page, size, totalElements, totalPages, first, last, empty.
 *
 * @param <T>            the type of elements contained in the page
 * @param content        elements of the current page; never null, may be empty
 * @param page           current page number (zero-based)
 * @param size           requested page size
 * @param totalElements  total number of elements across all pages
 * @param totalPages     total number of pages
 * @param first          whether this is the first page
 * @param last           whether this is the last page
 * @param empty          whether the current page has no content
 */
public record Page<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {

    /**
     * Compact constructor that enforces an unmodifiable, non-null content list.
     * A null content reference is normalized to an empty list to keep callers
     * free of null checks.
     */
    public Page {
        content = content == null ? List.of() : List.copyOf(content);
    }

    /**
     * Factory for building a page from raw data. Computes {@code totalPages},
     * {@code first}, {@code last} and {@code empty} from the provided values
     * so callers do not have to.
     *
     * @param content       elements of the current page
     * @param page          current page number (zero-based)
     * @param size          requested page size
     * @param totalElements total number of elements across all pages
     * @param <T>           the type of elements
     * @return a fully populated {@link Page}
     */
    public static <T> Page<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (size <= 0) ? 0 : (int) Math.ceil((double) totalElements / size);
        boolean isFirst = page == 0;
        boolean isLast = (totalPages == 0) || (page >= totalPages - 1);
        boolean isEmpty = content == null || content.isEmpty();
        return new Page<>(content, page, size, totalElements, totalPages, isFirst, isLast, isEmpty);
    }

    /**
     * Returns an empty page for the given pagination parameters. Useful when
     * a query has no results but the caller still expects pagination metadata.
     *
     * @param page current page number (zero-based)
     * @param size requested page size
     * @param <T>  the type of elements
     * @return an empty {@link Page} with zero total elements
     */
    public static <T> Page<T> empty(int page, int size) {
        return of(List.of(), page, size, 0L);
    }
}