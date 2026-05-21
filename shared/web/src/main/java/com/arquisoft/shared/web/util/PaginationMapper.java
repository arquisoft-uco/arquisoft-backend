package com.arquisoft.shared.web.util;

import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import com.arquisoft.shared.pagination.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utilidad de conversión entre los tipos de paginación de dominio y Spring Data JPA.
 *
 * <p>Usado exclusivamente en la capa de infraestructura (adaptadores JPA) para
 * mantener Spring Data fuera del dominio y la aplicación:</p>
 *
 * <ul>
 *   <li>{@link #toPageable(PaginationRequest)} — convierte el {@link PaginationRequest}
 *       del dominio al {@link Pageable} que necesita Spring Data JPA.</li>
 *   <li>{@link #toResult(Page)} — convierte el {@link Page} devuelto por JPA
 *       al {@link PaginatedResult} del dominio.</li>
 * </ul>
 *
 * <p>La conversión de los parámetros HTTP al {@link PaginationRequest} del dominio
 * es responsabilidad de {@code PaginationRequestArgumentResolver} en {@code shared:web}.</p>
 */
public final class PaginationMapper {

    private PaginationMapper() {}

    /**
     * Convierte un {@link PaginationRequest} del dominio al {@link Pageable} de Spring Data JPA.
     *
     * @param request solicitud de paginación del dominio (nunca {@code null})
     * @return {@link PageRequest} de Spring equivalente
     */
    public static Pageable toPageable(PaginationRequest request) {
        if (request.hasSort()) {
            Sort sort = Sort.by(
                    request.getDirection() == SortDirection.ASC
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC,
                    request.getSort());
            return PageRequest.of(request.getPage(), request.getSize(), sort);
        }
        return PageRequest.of(request.getPage(), request.getSize());
    }

    /**
     * Convierte un {@link Page} de Spring Data al {@link PaginatedResult} del dominio.
     *
     * @param page resultado paginado de Spring Data (nunca {@code null})
     * @return {@link PaginatedResult} equivalente
     */
    public static <T> PaginatedResult<T> toResult(Page<T> page) {
        return PaginatedResult.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }
}

