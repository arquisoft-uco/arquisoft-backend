package com.arquisoft.shared.query;

import java.util.List;

/**
 * Árbol de expresión booleana para filtros de consulta.
 *
 * Dos tipos de nodo:
 *   - Predicado: hoja del árbol — campo, operador y valor concreto.
 *   - Grupo: nodo interno — aplica un mismo conector (AND u OR)
 *            entre todos sus hijos de forma recursiva.
 *
 * Ejemplo: (titulo CONTIENE "web" OR asesorNombre CONTIENE "juan") AND asesorId ES uuid
 *
 *   Grupo(AND,
 *     Grupo(OR,
 *       Predicado("tituloProyecto", CONTIENE, "web"),
 *       Predicado("asesorNombre",   CONTIENE, "juan")),
 *     Predicado("asesorId", ES, "550e8400-..."))
 */
public sealed interface NodoFiltro permits NodoFiltro.Predicado, NodoFiltro.Grupo {

    record Predicado(
            String campo,
            FiltroOperador operador,
            String valor        // null cuando operador == ES_NULO | NO_ES_NULO
    ) implements NodoFiltro {}

    record Grupo(
            FiltroConector conector,
            List<NodoFiltro> nodos
    ) implements NodoFiltro {
        public Grupo {
            nodos = List.copyOf(nodos);
        }
    }

    static NodoFiltro predicado(String campo, FiltroOperador operador, String valor) {
        return new Predicado(campo, operador, valor);
    }

    static NodoFiltro grupo(FiltroConector conector, List<NodoFiltro> nodos) {
        return new Grupo(conector, nodos);
    }
}
