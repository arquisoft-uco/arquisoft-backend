package com.arquisoft.shared.web.dto.query;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Hoja del árbol de filtros: representa un predicado atómico.
 *
 * JSON esperado:
 * <pre>
 * { "tipo": "PREDICADO", "campo": "tituloProyecto", "operador": "CONTIENE", "valor": "web" }
 * { "tipo": "PREDICADO", "campo": "asesorId",       "operador": "ES_NULO" }
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredicadoFiltroDTO implements NodoFiltroDTO {

    private String campo;
    private FiltroOperador operador;
    private String valor;   // opcional: null para ES_NULO / NO_ES_NULO

    @Override
    public NodoFiltro toDomain() {
        return NodoFiltro.predicado(campo, operador, valor);
    }
}
