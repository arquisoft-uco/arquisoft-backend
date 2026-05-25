package com.arquisoft.shared.query;

import java.util.List;

public sealed interface NodoFiltro permits NodoFiltro.Predicado, NodoFiltro.Grupo {

    record Predicado(
            String campo,
            FiltroOperador operador,
            String valor
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
