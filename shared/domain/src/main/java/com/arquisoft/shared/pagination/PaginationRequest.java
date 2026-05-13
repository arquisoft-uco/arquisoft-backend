package com.arquisoft.shared.pagination;

import com.arquisoft.shared.exceptions.ApplicationException;
import com.arquisoft.shared.utils.UtilObject;
import com.arquisoft.shared.utils.UtilText;
import com.arquisoft.shared.utils.messages.AppMessages;

/**
 * Solicitud de consulta paginada — valor inmutable.
 *
 * <p>Encapsula los parámetros necesarios para realizar una consulta paginada:
 * número de página (0-based), tamaño, campo de ordenamiento (opcional)
 * y dirección de orden.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.
 * Usa {@link ApplicationException} (HTTP 400) para parámetros inválidos,
 * consistente con el handler global de la aplicación.</p>
 *
 * <p>Construcción exclusivamente via factories estáticos:
 * <ul>
 *   <li>{@link #of(int, int)} — sin ordenamiento</li>
 *   <li>{@link #of(int, int, String, SortDirection)} — con ordenamiento</li>
 * </ul>
 */
public final class PaginationRequest {

    private final int page;
    private final int size;
    private final String sort;
    private final SortDirection direction;

    private PaginationRequest(int page, int size, String sort, SortDirection direction) {
        if (page < 0) {
            throw new ApplicationException(
                    AppMessages.PaginationRequest.MENSAJE_PAGE_MAYOR_CERO, AppMessages.PaginationRequest.PAGE_INVALIDA);
        }
        if (size <= 0) {
            throw new ApplicationException(
                    AppMessages.PaginationRequest.MENSAJE_SIZE_MAYOR_CERO, AppMessages.PaginationRequest.SIZE_INVALIDA);
        }
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.direction = (!UtilObject.isNull(direction)) ? direction : SortDirection.ASC;
    }

    /**
     * Crea una solicitud de paginación sin ordenamiento explícito.
     *
     * @param page número de página 0-based
     * @param size número de elementos por página (debe ser > 0)
     */
    public static PaginationRequest of(int page, int size) {
        return new PaginationRequest(page, size, null, SortDirection.ASC);
    }

    /**
     * Crea una solicitud de paginación con campo y dirección de ordenamiento.
     *
     * @param page      número de página 0-based
     * @param size      número de elementos por página (debe ser > 0)
     * @param sort      nombre del campo por el que ordenar (puede ser {@code null})
     * @param direction dirección de ordenamiento; si es {@code null} se asume {@link SortDirection#ASC}
     */
    public static PaginationRequest of(int page, int size, String sort, SortDirection direction) {
        return new PaginationRequest(page, size, sort, direction);
    }

    /** Número de página 0-based. */
    public int getPage() {
        return page;
    }

    /** Número de elementos por página. */
    public int getSize() {
        return size;
    }

    /**
     * Campo de ordenamiento. Puede ser {@code null} si no se especificó ordenamiento.
     * Usar {@link #hasSort()} para verificar antes de consumir.
     */
    public String getSort() {
        return sort;
    }

    /** Dirección de ordenamiento. Nunca {@code null} — por defecto {@link SortDirection#ASC}. */
    public SortDirection getDirection() {
        return direction;
    }

    /** Retorna {@code true} si se especificó un campo de ordenamiento no vacío. */
    public boolean hasSort() {
        return !UtilText.isEmptyOrNull(sort);
    }
}
