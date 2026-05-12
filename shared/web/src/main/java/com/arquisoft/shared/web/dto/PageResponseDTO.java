package com.arquisoft.shared.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas. Convención de Spring Data:
 * content, page, size, totalElements, totalPages, first, last, empty.
 * Todos los endpoints paginados de la aplicación retornan este formato.
 *
 * <p>Nota: first, last y empty se declaran como Boolean (boxed) para evitar
 * el conflicto de Lombok @Data + @Builder con campos boolean primitivos
 * (Lombok omite del builder los campos cuyos getters tienen prefijo "is").
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
     * Factory desde un {@link Page} de Spring Data.
     * Permite usar: PageResponseDTO.from(springPage)
     *
     * <p>Es el único método factory de esta clase. Los controllers reciben un
     * {@code Page<T>} del use case y lo convierten aquí antes de retornar al cliente.
     */
    public static <T> PageResponseDTO<T> from(Page<T> page) {
        return PageResponseDTO.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
