package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorRecibidasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarSolicitudesNovedadCoordinadorRecibidasRequestMapper {

    private ConsultarSolicitudesNovedadCoordinadorRecibidasRequestMapper() {}

    public static ConsultarSolicitudesNovedadCoordinadorRecibidasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID coordinadorUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarSolicitudesNovedadCoordinadorRecibidasQuery.crear(coordinadorUsuario, criterio);
    }
}
