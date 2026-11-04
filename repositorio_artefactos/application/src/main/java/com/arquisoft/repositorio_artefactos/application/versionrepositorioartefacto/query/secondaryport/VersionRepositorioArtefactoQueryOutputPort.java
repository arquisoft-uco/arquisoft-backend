package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

import java.util.List;
import java.util.UUID;

public interface VersionRepositorioArtefactoQueryOutputPort {

    List<VersionRepositorioArtefactoReadModel> consultarPorRepositorio(UUID repositorioArtefacto);

    PaginatedResult<VersionRepositorioArtefactoAsesorReadModel> consultarParaAsesor(
            VersionRepositorioArtefactoAsesorCriteria criteria);
}
