package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoCoordinadorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarProyectosGradoCoordinadorRequestMapper {

    private ConsultarProyectosGradoCoordinadorRequestMapper() {}

    public static ConsultarProyectosGradoCoordinadorQuery toQuery(QueryCriteriaRequestDTO dto, UUID coordinador) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarProyectosGradoCoordinadorQuery.crear(coordinador, criterio);
    }
}
