package com.arquisoft.shared.web.util;

import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import com.arquisoft.shared.pagination.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utilidad de conversión entre los tipos de paginación de dominio y Spring Data.
 *
 * <p>Centraliza todas las conversiones Spring ↔ domain para que ni los controllers
 * ni los adapters tengan lógica de conversión dispersa:</p>
 *
 * <ul>
 *   <li>{@link #toDomain(Pageable)} — convierte el {@link Pageable} de Spring MVC
 *       al {@link PaginationRequest} del dominio.</li>
 *   <li>{@link #toPageable(PaginationRequest)} — convierte el {@link PaginationRequest}
 *       del dominio al {@link Pageable} de Spring Data JPA.</li>
 *   <li>{@link #toResult(Page)} — convierte un {@link Page} de Spring Data
 *       al {@link PaginatedResult} del dominio.</li>
 * </ul>
 *
 * <p>Esta clase vive en {@code shared:web} porque es la única capa que tiene
 * dependencias legítimas tanto de Spring Data Commons como de {@code shared:domain}.</p>
 */
public final class PaginationMapper {

    private PaginationMapper() {}

    /**
     * Convierte un {@link Pageable} de Spring MVC al {@link PaginationRequest} del dominio.
     *
     * <p>Solo se considera el primer campo de ordenamiento si el {@link Pageable}
     * tiene múltiples; los campos adicionales se ignoran.</p>
     *
     * @param pageable pageable recibido por el controller (nunca {@code null})
     * @return {@link PaginationRequest} equivalente
     */
    public static PaginationRequest toDomain(Pageable pageable) {
        String sortProperty = null;
        SortDirection direction = SortDirection.ASC;

        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order order = sort.iterator().next();
            sortProperty = order.getProperty();
            direction = order.isAscending() ? SortDirection.ASC : SortDirection.DESC;
        }

        return PaginationRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortProperty,
                direction);
    }

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
