package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.ArrayList;
import java.util.List;

final class FiltroCierreProyectoEntregable {

    private static final String FALSO = Boolean.FALSE.toString();

    private FiltroCierreProyectoEntregable() {}

    static NodoFiltro combinar(List<NodoFiltro> forzados, NodoFiltro raizCliente) {
        var claveCancelado = EntregableProyectoGradoCriteria.Campo.PROYECTO_CANCELADO.getClave();
        var claveFinalizado = EntregableProyectoGradoCriteria.Campo.PROYECTO_FINALIZADO.getClave();

        var nodos = new ArrayList<>(forzados);
        nodos.add(NodoFiltro.predicado(claveCancelado, FiltroOperador.ES, FALSO));
        if (!menciona(raizCliente, claveFinalizado)) {
            nodos.add(NodoFiltro.predicado(claveFinalizado, FiltroOperador.ES, FALSO));
        }
        if (UtilObjeto.noEsNulo(raizCliente)) {
            nodos.add(raizCliente);
        }
        return NodoFiltro.grupo(FiltroConector.AND, nodos);
    }

    private static boolean menciona(NodoFiltro nodo, String campo) {
        if (UtilObjeto.esNulo(nodo)) {
            return false;
        }
        return switch (nodo) {
            case NodoFiltro.Predicado predicado -> campo.equals(predicado.campo());
            case NodoFiltro.PredicadoMultivalor multivalor -> campo.equals(multivalor.campo());
            case NodoFiltro.Grupo grupo -> grupo.nodos().stream().anyMatch(hijo -> menciona(hijo, campo));
        };
    }
}
