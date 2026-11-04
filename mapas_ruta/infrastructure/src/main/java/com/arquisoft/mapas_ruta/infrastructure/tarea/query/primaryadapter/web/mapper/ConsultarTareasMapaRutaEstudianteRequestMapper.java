package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarTareasMapaRutaEstudianteRequestMapper {

    private ConsultarTareasMapaRutaEstudianteRequestMapper() {}

    public static ConsultarTareasMapaRutaEstudianteQuery toQuery(QueryCriteriaRequestDTO dto, UUID mapaRuta) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarTareasMapaRutaEstudianteQuery.crear(mapaRuta, criterio);
    }
}
