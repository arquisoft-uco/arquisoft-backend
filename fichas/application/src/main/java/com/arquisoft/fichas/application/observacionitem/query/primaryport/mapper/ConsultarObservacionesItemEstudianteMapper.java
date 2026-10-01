package com.arquisoft.fichas.application.observacionitem.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemEstudianteQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarObservacionesItemEstudianteMapper {

    private ConsultarObservacionesItemEstudianteMapper() {}

    public static ObservacionItemEstudianteCriteria toCriteria(ConsultarObservacionesItemEstudianteQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                ObservacionItemEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES,
                query.estudiante().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return ObservacionItemEstudianteCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
