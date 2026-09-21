package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesJuradoEstudianteQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarEvaluacionesJuradoEstudianteMapper {

    private ConsultarEvaluacionesJuradoEstudianteMapper() {}

    public static EvaluacionJuradoCriteria toCriteria(ConsultarEvaluacionesJuradoEstudianteQuery query) {
        var criterio = query.criterio();

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(EvaluacionJuradoCriteria.Campo.JURADO.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return EvaluacionJuradoCriteria.builder()
                .evaluacion(query.evaluacion())
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(criterio.raiz())
                .build();
    }
}
