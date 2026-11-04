package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesDelJuradoQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarEvaluacionesDelJuradoMapper {

    private ConsultarEvaluacionesDelJuradoMapper() {}

    public static EvaluacionDelJuradoCriteria toCriteria(ConsultarEvaluacionesDelJuradoQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                EvaluacionDelJuradoCriteria.Campo.JURADO_ID.getClave(), FiltroOperador.ES,
                query.jurado().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(EvaluacionDelJuradoCriteria.Campo.PROYECTO.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return EvaluacionDelJuradoCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raizFinal)
                .build();
    }
}
