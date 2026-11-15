package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionesAsignadasAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarEvaluacionesAsignadasAsesorRequestMapper {

    private ConsultarEvaluacionesAsignadasAsesorRequestMapper() {}

    public static ConsultarEvaluacionesAsignadasAsesorQuery toQuery(QueryCriteriaRequestDTO dto, UUID asesor) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEvaluacionesAsignadasAsesorQuery.crear(asesor, criterio);
    }
}
