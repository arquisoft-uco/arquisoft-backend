package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.DescargarArchivoVersionRepositorioArtefactoQuery;

import java.util.UUID;

public final class DescargarArchivoVersionRepositorioArtefactoRequestMapper {

    private DescargarArchivoVersionRepositorioArtefactoRequestMapper() {}

    public static DescargarArchivoVersionRepositorioArtefactoQuery toQuery(UUID version) {
        return DescargarArchivoVersionRepositorioArtefactoQuery.crear(version);
    }
}
