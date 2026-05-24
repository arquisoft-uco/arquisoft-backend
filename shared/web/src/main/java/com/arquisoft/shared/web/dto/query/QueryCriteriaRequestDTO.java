package com.arquisoft.shared.web.dto.query;

import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de entrada para endpoints de consulta paginada con filtros dinámicos.
 *
 * Encapsula todos los parámetros de una consulta: paginación, ordenamiento
 * y el árbol de filtros. Es reutilizable en cualquier contexto de negocio.
 *
 * Formato JSON:
 * <pre>
 * {
 *   "pagina": 0,
 *   "tamanio": 10,
 *   "ordenamiento": ["tituloProyecto:ASC"],
 *   "filtros": {
 *     "tipo": "GRUPO", "conector": "AND",
 *     "nodos": [...]
 *   }
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryCriteriaRequestDTO {

    private int pagina   = 0;
    private int tamanio  = 10;
    private List<String> ordenamiento;
    private NodoFiltroDTO filtros;

    public List<SortOrder> parsearOrdenamiento() {
        if (ordenamiento == null || ordenamiento.isEmpty()) {
            return List.of();
        }
        return ordenamiento.stream().map(SortOrder::parse).toList();
    }

    public NodoFiltro parsearFiltros() {
        return filtros != null ? filtros.toDomain() : null;
    }
}
