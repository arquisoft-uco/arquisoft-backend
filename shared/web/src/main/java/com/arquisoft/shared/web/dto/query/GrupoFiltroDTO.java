package com.arquisoft.shared.web.dto.query;

import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.NodoFiltro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Nodo interno del árbol de filtros: agrupa predicados u otros grupos con un conector.
 *
 * El campo "nodos" es polimórfico: cada elemento puede ser PREDICADO o GRUPO,
 * lo cual permite anidar grupos a cualquier profundidad.
 *
 * JSON esperado:
 * <pre>
 * {
 *   "tipo": "GRUPO", "conector": "OR",
 *   "nodos": [
 *     { "tipo": "PREDICADO", "campo": "tituloProyecto", "operador": "CONTIENE", "valor": "web" },
 *     { "tipo": "PREDICADO", "campo": "asesorNombre",   "operador": "CONTIENE", "valor": "juan" }
 *   ]
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrupoFiltroDTO implements NodoFiltroDTO {

    private FiltroConector conector;
    private List<NodoFiltroDTO> nodos;

    @Override
    public NodoFiltro toDomain() {
        return NodoFiltro.grupo(
                conector,
                nodos.stream().map(NodoFiltroDTO::toDomain).toList()
        );
    }
}
