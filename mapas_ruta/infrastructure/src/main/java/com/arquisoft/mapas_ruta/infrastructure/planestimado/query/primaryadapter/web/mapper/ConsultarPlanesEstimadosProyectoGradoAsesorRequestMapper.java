package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosProyectoGradoAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarPlanesEstimadosProyectoGradoAsesorRequestMapper {

    private ConsultarPlanesEstimadosProyectoGradoAsesorRequestMapper() {}

    public static ConsultarPlanesEstimadosProyectoGradoAsesorQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID proyectoGrado) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarPlanesEstimadosProyectoGradoAsesorQuery.crear(proyectoGrado, criterio);
    }
}
