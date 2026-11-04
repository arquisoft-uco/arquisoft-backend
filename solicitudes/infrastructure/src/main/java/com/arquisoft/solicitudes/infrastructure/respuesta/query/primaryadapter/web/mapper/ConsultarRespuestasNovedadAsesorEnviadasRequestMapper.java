package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadAsesorEnviadasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRespuestasNovedadAsesorEnviadasRequestMapper {

    private ConsultarRespuestasNovedadAsesorEnviadasRequestMapper() {}

    public static ConsultarRespuestasNovedadAsesorEnviadasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID asesorUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRespuestasNovedadAsesorEnviadasQuery.crear(asesorUsuario, criterio);
    }
}
