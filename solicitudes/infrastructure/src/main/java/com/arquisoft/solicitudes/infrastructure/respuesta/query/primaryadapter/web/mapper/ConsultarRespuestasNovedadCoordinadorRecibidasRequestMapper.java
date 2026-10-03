package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadCoordinadorRecibidasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRespuestasNovedadCoordinadorRecibidasRequestMapper {

    private ConsultarRespuestasNovedadCoordinadorRecibidasRequestMapper() {}

    public static ConsultarRespuestasNovedadCoordinadorRecibidasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID estudianteUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRespuestasNovedadCoordinadorRecibidasQuery.crear(estudianteUsuario, criterio);
    }
}
