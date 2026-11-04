package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarEvaluacionesCoordinadorMapper {

    private ConsultarEvaluacionesCoordinadorMapper() {}

    public static EvaluacionCriteria toCriteria(ConsultaCriteriaQuery query) {
        var ordenamiento = UtilColeccion.esVaciaONula(query.ordenamiento())
                ? List.of(SortOrder.of(EvaluacionCriteria.Campo.PROYECTO.getClave(), SortDirection.ASC))
                : query.ordenamiento();

        return EvaluacionCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(query.raiz())
                .build();
    }
}
