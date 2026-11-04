package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ConsultarTiposSolicitudRequestMapper {

    private static final String NOVEDAD_PARA_EL_COORDINADOR = "NOVEDAD_PARA_EL_COORDINADOR";
    private static final String NOVEDAD_PARA_EL_ASESOR = "NOVEDAD_PARA_EL_ASESOR";
    private static final String CAMBIO_DE_ASESOR = "CAMBIO_DE_ASESOR";
    private static final String AMPLIACION_DE_PLAZO = "AMPLIACION_DE_PLAZO";
    private static final String REGISTRO_Y_MODIFICACION_DE_USUARIOS = "REGISTRO_Y_MODIFICACION_DE_USUARIOS";

    private static final Map<String, Set<String>> TIPOS_DE_ENVIO_POR_ROL_IDENTIDAD = Map.of(
            UsuariosRoles.ESTUDIANTE, Set.of(
                    NOVEDAD_PARA_EL_COORDINADOR, NOVEDAD_PARA_EL_ASESOR, CAMBIO_DE_ASESOR, AMPLIACION_DE_PLAZO),
            UsuariosRoles.COORDINADOR, Set.of(REGISTRO_Y_MODIFICACION_DE_USUARIOS));

    private ConsultarTiposSolicitudRequestMapper() {}

    public static ConsultarTiposSolicitudQuery toQuery(IdentidadActual identidad) {
        var tipos = RolesIdentidad.traducir(identidad, TIPOS_DE_ENVIO_POR_ROL_IDENTIDAD).stream()
                .flatMap(Set::stream)
                .collect(Collectors.toUnmodifiableSet());
        return ConsultarTiposSolicitudQuery.crear(tipos);
    }
}
