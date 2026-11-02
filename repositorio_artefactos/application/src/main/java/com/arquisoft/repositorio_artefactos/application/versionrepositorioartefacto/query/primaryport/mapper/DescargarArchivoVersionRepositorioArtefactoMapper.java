package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.ArchivoVersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.DescargarArchivoVersionRepositorioArtefactoQuery;

public final class DescargarArchivoVersionRepositorioArtefactoMapper {

    private DescargarArchivoVersionRepositorioArtefactoMapper() {}

    public static ArchivoVersionRepositorioArtefactoCriteria toCriteria(
            DescargarArchivoVersionRepositorioArtefactoQuery query) {
        return new ArchivoVersionRepositorioArtefactoCriteria(query.version());
    }
}
