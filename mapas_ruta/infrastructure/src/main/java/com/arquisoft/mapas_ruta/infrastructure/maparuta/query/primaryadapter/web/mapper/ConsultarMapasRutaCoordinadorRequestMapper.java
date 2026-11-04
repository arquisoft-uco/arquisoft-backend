package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarMapasRutaCoordinadorRequestMapper {

    private ConsultarMapasRutaCoordinadorRequestMapper() {}

    public static ConsultarMapasRutaCoordinadorQuery toQuery(QueryCriteriaRequestDTO dto, UUID coordinador) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarMapasRutaCoordinadorQuery.crear(coordinador, criterio);
    }
}
