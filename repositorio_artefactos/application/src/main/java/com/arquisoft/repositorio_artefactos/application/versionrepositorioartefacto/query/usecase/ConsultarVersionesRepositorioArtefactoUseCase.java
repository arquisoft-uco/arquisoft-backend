package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarVersionesRepositorioArtefactoUseCase
        extends UseCase<VersionRepositorioArtefactoCriteria, List<VersionRepositorioArtefactoReadModel>> {}
