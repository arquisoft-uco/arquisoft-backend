package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesRoles;

public final class ConsultarTiposSolicitudRequestMapper {

    private ConsultarTiposSolicitudRequestMapper() {}

    public static ConsultarTiposSolicitudQuery toQuery(IdentidadActual identidad) {
        return ConsultarTiposSolicitudQuery.crear(
                identidad.id(),
                RolesIdentidad.traducir(identidad, SolicitudesRoles.NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
