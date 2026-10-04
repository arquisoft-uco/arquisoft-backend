package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarObservacionesItemEstudianteRequestMapper {

    private ConsultarObservacionesItemEstudianteRequestMapper() {}

    public static ConsultarObservacionesItemEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID estudiante) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarObservacionesItemEstudianteQuery.crear(estudiante, criterio);
    }
}
