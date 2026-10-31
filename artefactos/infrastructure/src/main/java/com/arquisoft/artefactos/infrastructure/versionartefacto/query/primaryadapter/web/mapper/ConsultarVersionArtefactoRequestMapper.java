package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;

import java.util.Map;
import java.util.UUID;

public final class ConsultarVersionArtefactoRequestMapper {

    private static final Map<String, RolConsultaVersionArtefacto> ROLES_NEGOCIO_POR_ROL_IDENTIDAD = Map.of(
            UsuariosRoles.ASESOR, RolConsultaVersionArtefacto.ASESOR,
            UsuariosRoles.ESTUDIANTE, RolConsultaVersionArtefacto.ESTUDIANTE);

    private ConsultarVersionArtefactoRequestMapper() {}

    public static ConsultarVersionArtefactoQuery toQuery(UUID versionArtefacto, IdentidadActual identidad) {
        return ConsultarVersionArtefactoQuery.crear(
                versionArtefacto, identidad.id(),
                RolesIdentidad.traducir(identidad, ROLES_NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
