package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarObservacionesItemAsesorRequestMapper {

    private ConsultarObservacionesItemAsesorRequestMapper() {}

    public static ConsultarObservacionesItemAsesorQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID evaluacionCuantitativaAsesor, UUID asesor) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarObservacionesItemAsesorQuery.crear(evaluacionCuantitativaAsesor, asesor, criterio);
    }
}
