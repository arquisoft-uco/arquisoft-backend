package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesRoles;

import java.util.Map;

public final class ConsultarTiposSolicitudRequestMapper {

    private static final Map<String, String> ROLES_NEGOCIO_POR_ROL_IDENTIDAD = Map.of(
            UsuariosRoles.ESTUDIANTE, SolicitudesRoles.Negocio.ESTUDIANTE,
            UsuariosRoles.COORDINADOR, SolicitudesRoles.Negocio.COORDINADOR,
            UsuariosRoles.ASESOR, SolicitudesRoles.Negocio.ASESOR,
            UsuariosRoles.ADMINISTRADOR, SolicitudesRoles.Negocio.ADMINISTRADOR);

    private ConsultarTiposSolicitudRequestMapper() {}

    public static ConsultarTiposSolicitudQuery toQuery(IdentidadActual identidad) {
        return ConsultarTiposSolicitudQuery.crear(
                identidad.id(),
                RolesIdentidad.traducir(identidad, ROLES_NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
