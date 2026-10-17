package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesJuradoEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarEvaluacionesJuradoRequestMapper {

    private ConsultarEvaluacionesJuradoRequestMapper() {}

    public static ConsultarEvaluacionesJuradoEstudianteQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID evaluacionId) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEvaluacionesJuradoEstudianteQuery.crear(evaluacionId, criterio);
    }
}
