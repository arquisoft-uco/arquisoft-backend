package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRevisionesItemEstudianteRequestMapper {

    private ConsultarRevisionesItemEstudianteRequestMapper() {}

    public static ConsultarRevisionesItemEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID estudiante) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRevisionesItemEstudianteQuery.crear(estudiante, criterio);
    }
}
