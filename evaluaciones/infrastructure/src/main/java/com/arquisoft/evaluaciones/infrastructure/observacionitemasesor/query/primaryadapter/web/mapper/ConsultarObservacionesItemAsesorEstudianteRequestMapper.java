package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarObservacionesItemAsesorEstudianteRequestMapper {

    private ConsultarObservacionesItemAsesorEstudianteRequestMapper() {}

    public static ConsultarObservacionesItemAsesorEstudianteQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID evaluacionCuantitativaAsesor, UUID estudiante) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarObservacionesItemAsesorEstudianteQuery.crear(
                evaluacionCuantitativaAsesor, estudiante, criterio);
    }
}
