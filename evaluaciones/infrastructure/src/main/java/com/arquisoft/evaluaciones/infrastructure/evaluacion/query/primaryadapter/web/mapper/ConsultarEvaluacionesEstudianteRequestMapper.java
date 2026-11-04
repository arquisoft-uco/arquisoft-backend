package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarEvaluacionesEstudianteRequestMapper {

    private ConsultarEvaluacionesEstudianteRequestMapper() {}

    public static ConsultarEvaluacionesEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID estudiante) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEvaluacionesEstudianteQuery.crear(estudiante, criterio);
    }
}
