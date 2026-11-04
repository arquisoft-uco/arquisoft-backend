package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarProyectosGradoAsesorRequestMapper {

    private ConsultarProyectosGradoAsesorRequestMapper() {}

    public static ConsultarProyectosGradoAsesorQuery toQuery(QueryCriteriaRequestDTO dto, UUID asesor) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarProyectosGradoAsesorQuery.crear(asesor, criterio);
    }
}
