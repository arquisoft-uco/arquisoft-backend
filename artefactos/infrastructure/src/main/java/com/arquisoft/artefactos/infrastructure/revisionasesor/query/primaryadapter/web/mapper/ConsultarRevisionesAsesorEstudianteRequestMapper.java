package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRevisionesAsesorEstudianteRequestMapper {

    private ConsultarRevisionesAsesorEstudianteRequestMapper() {}

    public static ConsultarRevisionesAsesorEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID estudiante) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRevisionesAsesorEstudianteQuery.crear(estudiante, criterio);
    }
}
