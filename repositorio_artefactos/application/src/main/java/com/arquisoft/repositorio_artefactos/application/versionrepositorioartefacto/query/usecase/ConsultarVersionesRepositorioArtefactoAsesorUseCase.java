package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarVersionesRepositorioArtefactoAsesorUseCase
        extends UseCase<VersionRepositorioArtefactoAsesorCriteria,
                PaginatedResult<VersionRepositorioArtefactoAsesorReadModel>> {}
