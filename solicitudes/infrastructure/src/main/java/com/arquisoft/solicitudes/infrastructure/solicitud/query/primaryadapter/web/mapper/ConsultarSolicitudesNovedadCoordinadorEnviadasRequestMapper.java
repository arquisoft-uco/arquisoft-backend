package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorEnviadasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarSolicitudesNovedadCoordinadorEnviadasRequestMapper {

    private ConsultarSolicitudesNovedadCoordinadorEnviadasRequestMapper() {}

    public static ConsultarSolicitudesNovedadCoordinadorEnviadasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID estudianteUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarSolicitudesNovedadCoordinadorEnviadasQuery.crear(estudianteUsuario, criterio);
    }
}
