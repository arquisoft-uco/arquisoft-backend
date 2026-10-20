package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;
import com.arquisoft.fichas.infrastructure.security.FichasRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.util.Map;

public final class ConsultarEstadosFichaRequestMapper {

    private static final Map<String, String> ROLES_NEGOCIO_POR_ROL_IDENTIDAD = Map.of(
            FichasRoles.Identidad.ASESOR_FICHA, FichasRoles.Negocio.ASESOR_FICHA,
            FichasRoles.Identidad.COORDINADOR, FichasRoles.Negocio.COORDINADOR,
            FichasRoles.Identidad.REPRESENTANTE_COMITE, FichasRoles.Negocio.REPRESENTANTE_COMITE);

    private ConsultarEstadosFichaRequestMapper() {}

    public static ConsultarEstadosFichaQuery toQuery(IdentidadActual identidad) {
        var rolesDeIdentidad = identidad.roles().stream()
                .filter(ROLES_NEGOCIO_POR_ROL_IDENTIDAD::containsKey)
                .map(ROLES_NEGOCIO_POR_ROL_IDENTIDAD::get)
                .distinct()
                .toList();

        return ConsultarEstadosFichaQuery.crear(identidad.id(), rolesDeIdentidad);
    }
}
