package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarEvaluacionesAsignadasJuradoMapper {

    private static final List<SortOrder> ORDENAMIENTO_POR_DEFECTO = List.of(
            SortOrder.of(EvaluacionesAsignadasJuradoCriteria.Campo.ESTADO_JURADO.getClave(), SortDirection.ASC),
            SortOrder.of(EvaluacionesAsignadasJuradoCriteria.Campo.ESTADO.getClave(), SortDirection.ASC),
            SortOrder.of(EvaluacionesAsignadasJuradoCriteria.Campo.PROYECTO.getClave(), SortDirection.ASC),
            SortOrder.of(EvaluacionesAsignadasJuradoCriteria.Campo.VERSION.getClave(), SortDirection.ASC));

    private ConsultarEvaluacionesAsignadasJuradoMapper() {}

    public static EvaluacionesAsignadasJuradoCriteria toCriteria(ConsultarEvaluacionesAsignadasJuradoQuery query) {
        var criterio = query.criterio();

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? ORDENAMIENTO_POR_DEFECTO
                : criterio.ordenamiento();

        return EvaluacionesAsignadasJuradoCriteria.builder()
                .jurado(query.jurado())
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(criterio.raiz())
                .build();
    }
}
