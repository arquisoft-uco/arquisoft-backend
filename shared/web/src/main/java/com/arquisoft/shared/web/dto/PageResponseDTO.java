package com.arquisoft.shared.web.dto;

import com.arquisoft.shared.pagination.PaginatedResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas.
 * Formato estándar: content, page, size, totalElements, totalPages, first, last, empty.
 * Todos los endpoints paginados de la aplicación retornan este formato.
 *
 * <p>Nota: first, last y empty se declaran como Boolean (boxed) para evitar
 * el conflicto de Lombok @Data + @Builder con campos boolean primitivos
 * (Lombok omite del builder los campos cuyos getters tienen prefijo "is").
 * {@code @JsonInclude(NON_NULL)} evita que esos campos aparezcan como {@code null}
 * en el JSON si el DTO se construye sin el factory.</p>
 */
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

    /**
     * Factory desde un {@link PaginatedResult} del dominio.
     * Es el único punto de entrada para construir este DTO a partir de un resultado paginado.
     */
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
