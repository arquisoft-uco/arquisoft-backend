package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesTareaEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarPlanesRealesTareaEstudianteRequestMapper {

    private ConsultarPlanesRealesTareaEstudianteRequestMapper() {}

    public static ConsultarPlanesRealesTareaEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID tarea) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarPlanesRealesTareaEstudianteQuery.crear(tarea, criterio);
    }
}
