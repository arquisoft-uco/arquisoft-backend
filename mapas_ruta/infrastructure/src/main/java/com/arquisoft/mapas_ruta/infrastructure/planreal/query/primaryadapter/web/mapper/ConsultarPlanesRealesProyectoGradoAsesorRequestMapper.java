package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesProyectoGradoAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarPlanesRealesProyectoGradoAsesorRequestMapper {

    private ConsultarPlanesRealesProyectoGradoAsesorRequestMapper() {}

    public static ConsultarPlanesRealesProyectoGradoAsesorQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID proyectoGrado) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarPlanesRealesProyectoGradoAsesorQuery.crear(proyectoGrado, criterio);
    }
}
