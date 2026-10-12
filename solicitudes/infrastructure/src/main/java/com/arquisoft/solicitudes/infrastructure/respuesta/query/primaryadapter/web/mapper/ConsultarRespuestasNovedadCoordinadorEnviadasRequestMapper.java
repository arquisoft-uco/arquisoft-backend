package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadCoordinadorEnviadasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRespuestasNovedadCoordinadorEnviadasRequestMapper {

    private ConsultarRespuestasNovedadCoordinadorEnviadasRequestMapper() {}

    public static ConsultarRespuestasNovedadCoordinadorEnviadasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID coordinadorUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRespuestasNovedadCoordinadorEnviadasQuery.crear(coordinadorUsuario, criterio);
    }
}
