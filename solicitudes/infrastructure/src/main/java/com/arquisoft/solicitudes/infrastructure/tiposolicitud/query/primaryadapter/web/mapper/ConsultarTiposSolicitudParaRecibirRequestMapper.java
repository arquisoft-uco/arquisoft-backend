package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudParaRecibirQuery;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesRoles;

public final class ConsultarTiposSolicitudParaRecibirRequestMapper {

    private ConsultarTiposSolicitudParaRecibirRequestMapper() {}

    public static ConsultarTiposSolicitudParaRecibirQuery toQuery(IdentidadActual identidad) {
        return ConsultarTiposSolicitudParaRecibirQuery.crear(
                identidad.id(),
                RolesIdentidad.traducir(identidad, SolicitudesRoles.NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
