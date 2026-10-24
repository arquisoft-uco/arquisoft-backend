package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;
import com.arquisoft.fichas.infrastructure.security.FichasRoles;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;

import java.util.Map;

public final class ConsultarEstadosFichaRequestMapper {

    private static final Map<String, String> ROLES_NEGOCIO_POR_ROL_IDENTIDAD = Map.of(
            UsuariosRoles.ASESOR_FICHA, FichasRoles.Negocio.ASESOR_FICHA,
            UsuariosRoles.COORDINADOR, FichasRoles.Negocio.COORDINADOR,
            UsuariosRoles.REPRESENTANTE_COMITE, FichasRoles.Negocio.REPRESENTANTE_COMITE);

    private ConsultarEstadosFichaRequestMapper() {}

    public static ConsultarEstadosFichaQuery toQuery(IdentidadActual identidad) {
        return ConsultarEstadosFichaQuery.crear(
                identidad.id(), RolesIdentidad.traducir(identidad, ROLES_NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
