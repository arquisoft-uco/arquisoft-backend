package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasCambioAsesorRecibidasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRespuestasCambioAsesorRecibidasRequestMapper {

    private ConsultarRespuestasCambioAsesorRecibidasRequestMapper() {}

    public static ConsultarRespuestasCambioAsesorRecibidasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID estudianteUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRespuestasCambioAsesorRecibidasQuery.crear(estudianteUsuario, criterio);
    }
}
