package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarTareasMapaRutaAsesorRequestMapper {

    private ConsultarTareasMapaRutaAsesorRequestMapper() {}

    public static ConsultarTareasMapaRutaAsesorQuery toQuery(QueryCriteriaRequestDTO dto, UUID mapaRuta) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarTareasMapaRutaAsesorQuery.crear(mapaRuta, criterio);
    }
}
