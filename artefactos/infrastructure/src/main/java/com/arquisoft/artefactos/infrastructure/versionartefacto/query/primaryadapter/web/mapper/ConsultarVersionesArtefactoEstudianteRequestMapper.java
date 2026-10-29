package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarVersionesArtefactoEstudianteRequestMapper {

    private ConsultarVersionesArtefactoEstudianteRequestMapper() {}

    public static ConsultarVersionesArtefactoEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID estudiante) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarVersionesArtefactoEstudianteQuery.crear(estudiante, criterio);
    }
}
