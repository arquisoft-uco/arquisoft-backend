package com.arquisoft.seguridad.application.dto;

import java.util.List;

/**
 * DTO genérico de paginación que encapsula los metadatos de una página de resultados
 * junto con el contenido de la misma.
 *
 * <p>Modelado como {@code record} genérico de Java 21. Reutilizable en otros contextos
 * que requieran respuestas paginadas.
 *
 * @param <T>             tipo de los elementos del contenido
 * @param totalElementos  total de registros que cumplen los filtros (para {@code X-Total-Count})
 * @param numeroPagina    número de página actual (0-indexed, para {@code X-Page-Number})
 * @param tamanoPagina    tamaño de la página (para {@code X-Page-Size})
 * @param contenido       lista de DTOs de la página actual
 */
public record PaginaResponseDTO<T>(
        long totalElementos,
        int numeroPagina,
        int tamanoPagina,
        List<T> contenido
) {

    /**
     * Factory method para construir el DTO de paginación a partir de los datos
     * retornados por el repositorio.
     *
     * @param <T>      tipo de los elementos del contenido
     * @param total    total de registros que cumplen los filtros
     * @param pagina   número de página actual (0-indexed)
     * @param tamano   tamaño de la página
     * @param contenido lista de DTOs de la página actual
     * @return instancia de {@code PaginaResponseDTO} con los metadatos y contenido
     */
    public static <T> PaginaResponseDTO<T> fromData(
            long total,
            int pagina,
            int tamano,
            List<T> contenido) {
        return new PaginaResponseDTO<>(total, pagina, tamano, contenido);
    }
}
