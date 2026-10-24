package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.ConsultarVersionesRepositorioArtefactoQuery;

public final class ConsultarVersionesRepositorioArtefactoMapper {

    private ConsultarVersionesRepositorioArtefactoMapper() {}

    public static VersionRepositorioArtefactoCriteria toCriteria(ConsultarVersionesRepositorioArtefactoQuery query) {
        return new VersionRepositorioArtefactoCriteria(query.repositorioArtefacto());
    }
}
