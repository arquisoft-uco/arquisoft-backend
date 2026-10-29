package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRespuestasRegistroModificacionUsuariosEnviadasRequestMapper {

    private ConsultarRespuestasRegistroModificacionUsuariosEnviadasRequestMapper() {}

    public static ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID administradorUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery.crear(administradorUsuario, criterio);
    }
}
