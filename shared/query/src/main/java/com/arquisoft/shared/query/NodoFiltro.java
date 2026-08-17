package com.arquisoft.shared.query;

import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public sealed interface NodoFiltro permits NodoFiltro.Predicado, NodoFiltro.PredicadoMultivalor, NodoFiltro.Grupo {

    record Predicado(
            String campo,
            FiltroOperador operador,
            String valor
    ) implements NodoFiltro {}

    record PredicadoMultivalor(
            String campo,
            FiltroOperador operador,
            List<String> valores
    ) implements NodoFiltro {
        public PredicadoMultivalor {
            valores = UtilColeccion.aplicarPorDefecto(valores);
        }
    }

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

    static NodoFiltro predicadoMultivalor(String campo, FiltroOperador operador, List<String> valores) {
        return new PredicadoMultivalor(campo, operador, valores);
    }

    static NodoFiltro grupo(FiltroConector conector, List<NodoFiltro> nodos) {
        return new Grupo(conector, nodos);
    }
}
