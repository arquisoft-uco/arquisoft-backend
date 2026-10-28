package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarEvaluacionesAsignadasJuradoRequestMapper {

    private ConsultarEvaluacionesAsignadasJuradoRequestMapper() {}

    public static ConsultarEvaluacionesAsignadasJuradoQuery toQuery(QueryCriteriaRequestDTO dto, UUID jurado) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEvaluacionesAsignadasJuradoQuery.crear(jurado, criterio);
    }
}
