package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.DescargarArchivoVersionArtefactoQuery;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;

import java.util.UUID;

public final class DescargarArchivoVersionArtefactoRequestMapper {

    private DescargarArchivoVersionArtefactoRequestMapper() {}

    public static DescargarArchivoVersionArtefactoQuery toQuery(UUID versionArtefacto, IdentidadActual identidad) {
        return DescargarArchivoVersionArtefactoQuery.crear(
                versionArtefacto, identidad.id(),
                RolesIdentidad.traducir(identidad,
                        ConsultarVersionArtefactoRequestMapper.ROLES_NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
