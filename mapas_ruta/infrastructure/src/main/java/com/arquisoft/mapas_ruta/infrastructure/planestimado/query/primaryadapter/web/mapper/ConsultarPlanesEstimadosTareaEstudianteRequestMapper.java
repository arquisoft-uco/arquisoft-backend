package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarPlanesEstimadosTareaEstudianteRequestMapper {

    private ConsultarPlanesEstimadosTareaEstudianteRequestMapper() {}

    public static ConsultarPlanesEstimadosTareaEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID tarea) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarPlanesEstimadosTareaEstudianteQuery.crear(tarea, criterio);
    }
}
