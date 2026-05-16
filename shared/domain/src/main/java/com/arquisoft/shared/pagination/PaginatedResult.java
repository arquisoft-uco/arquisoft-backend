package com.arquisoft.shared.pagination;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Resultado paginado genérico — valor inmutable.
 *
 * <p>Contiene una porción de resultados ({@code content}) junto con los
 * metadatos de paginación: número de página, tamaño, total de elementos,
 * total de páginas, y flags de primera/última página.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 *
 * <p>Construcción via factory estático:
 * <ul>
 *   <li>{@link #of(List, int, int, long)} — calcula automáticamente los metadatos
 *       derivados ({@code totalPages}, {@code first}, {@code last}, {@code empty}).</li>
 * </ul>
 * </p>
 *
 * <p>El método {@link #map(Function)} transforma el contenido preservando
 * los metadatos de paginación, equivalente a {@code Page.map()} de Spring.</p>
 */
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

    /**
     * Crea un resultado paginado calculando automáticamente los metadatos derivados.
     *
     * <p>{@code totalPages} se calcula como ⌈totalElements / size⌉.
     * {@code first} es {@code true} cuando {@code page == 0}.
     * {@code last} es {@code true} cuando {@code page >= totalPages - 1}.</p>
     *
     * @param content       elementos de la página actual
     * @param page          número de página 0-based
     * @param size          tamaño de página solicitado
     * @param totalElements total de elementos en el conjunto completo
     */
    public static <T> PaginatedResult<T> of(List<T> content, int page, int size, long totalElements) {
        return new PaginatedResult<>(content, page, size, totalElements);
    }

    /**
     * Transforma el contenido de esta página aplicando {@code mapper} a cada elemento.
     *
     * <p>Los metadatos de paginación ({@code page}, {@code size}, {@code totalElements},
     * {@code totalPages}, {@code first}, {@code last}, {@code empty}) se preservan
     * sin cambios — solo cambia el tipo genérico del contenido.</p>
     *
     * @param mapper función de transformación de {@code T} a {@code U}
     * @return nuevo {@link PaginatedResult} con el contenido transformado
     */
    public <U> PaginatedResult<U> map(Function<T, U> mapper) {
        List<U> mappedContent = this.content.stream()
                .map(mapper)
                .toList();
        return new PaginatedResult<>(mappedContent, this.page, this.size, this.totalElements);
    }

    /** Elementos de la página actual. Lista inmutable. */
    public List<T> getContent() {
        return content;
    }

    /** Número de página 0-based. */
    public int getPage() {
        return page;
    }

    /** Tamaño de página (número de elementos por página). */
    public int getSize() {
        return size;
    }

    /** Total de elementos en el conjunto completo (no solo esta página). */
    public long getTotalElements() {
        return totalElements;
    }

    /** Número total de páginas disponibles. */
    public int getTotalPages() {
        return totalPages;
    }

    /** {@code true} si esta es la primera página (page == 0). */
    public boolean isFirst() {
        return first;
    }

    /** {@code true} si esta es la última página. */
    public boolean isLast() {
        return last;
    }

    /** {@code true} si la lista de contenido está vacía. */
    public boolean isEmpty() {
        return empty;
    }
}
