package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionesAsignadasAsesorQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarEvaluacionesAsignadasAsesorMapper {

    private static final List<SortOrder> ORDENAMIENTO_POR_DEFECTO = List.of(
            SortOrder.of(EvaluacionesAsignadasAsesorCriteria.Campo.ESTADO.getClave(), SortDirection.ASC),
            SortOrder.of(EvaluacionesAsignadasAsesorCriteria.Campo.PROYECTO.getClave(), SortDirection.ASC),
            SortOrder.of(EvaluacionesAsignadasAsesorCriteria.Campo.VERSION.getClave(), SortDirection.ASC));

    private ConsultarEvaluacionesAsignadasAsesorMapper() {}

    public static EvaluacionesAsignadasAsesorCriteria toCriteria(ConsultarEvaluacionesAsignadasAsesorQuery query) {
        var criterio = query.criterio();

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? ORDENAMIENTO_POR_DEFECTO
                : criterio.ordenamiento();

        return EvaluacionesAsignadasAsesorCriteria.builder()
                .asesor(query.asesor())
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(criterio.raiz())
                .build();
    }
}
