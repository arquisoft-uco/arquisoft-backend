package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadAsesorRecibidasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarSolicitudesNovedadAsesorRecibidasRequestMapper {

    private ConsultarSolicitudesNovedadAsesorRecibidasRequestMapper() {}

    public static ConsultarSolicitudesNovedadAsesorRecibidasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID asesorUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarSolicitudesNovedadAsesorRecibidasQuery.crear(asesorUsuario, criterio);
    }
}
