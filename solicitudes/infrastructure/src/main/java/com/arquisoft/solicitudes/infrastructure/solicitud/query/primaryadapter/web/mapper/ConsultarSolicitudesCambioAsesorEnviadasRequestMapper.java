package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesCambioAsesorEnviadasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarSolicitudesCambioAsesorEnviadasRequestMapper {

    private ConsultarSolicitudesCambioAsesorEnviadasRequestMapper() {}

    public static ConsultarSolicitudesCambioAsesorEnviadasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID estudianteUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarSolicitudesCambioAsesorEnviadasQuery.crear(estudianteUsuario, criterio);
    }
}
