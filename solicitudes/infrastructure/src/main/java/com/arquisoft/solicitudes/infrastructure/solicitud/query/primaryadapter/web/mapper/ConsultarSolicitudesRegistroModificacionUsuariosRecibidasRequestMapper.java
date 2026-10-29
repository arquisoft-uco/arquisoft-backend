package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarSolicitudesRegistroModificacionUsuariosRecibidasRequestMapper {

    private ConsultarSolicitudesRegistroModificacionUsuariosRecibidasRequestMapper() {}

    public static ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID administradorUsuario) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery.crear(administradorUsuario, criterio);
    }
}
