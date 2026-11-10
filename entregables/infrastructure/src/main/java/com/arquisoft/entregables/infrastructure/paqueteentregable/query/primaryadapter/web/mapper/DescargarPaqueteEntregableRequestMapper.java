package com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.DescargarPaqueteEntregableQuery;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.RolConsultaPaqueteEntregable;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;

import java.util.Map;
import java.util.UUID;

public final class DescargarPaqueteEntregableRequestMapper {

    static final Map<String, RolConsultaPaqueteEntregable> ROLES_NEGOCIO_POR_ROL_IDENTIDAD = Map.of(
            UsuariosRoles.ASESOR, RolConsultaPaqueteEntregable.ASESOR,
            UsuariosRoles.ESTUDIANTE, RolConsultaPaqueteEntregable.ESTUDIANTE);

    private DescargarPaqueteEntregableRequestMapper() {}

    public static DescargarPaqueteEntregableQuery toQuery(UUID entregableProyectoGrado, IdentidadActual identidad) {
        return DescargarPaqueteEntregableQuery.crear(
                entregableProyectoGrado, identidad.id(),
                RolesIdentidad.traducir(identidad, ROLES_NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
