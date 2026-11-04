package com.arquisoft.artefactos.application.versionartefacto.query.usecase;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarVersionArtefactoUseCase
        extends UseCase<VersionArtefactoDetalleCriteria, VersionArtefactoReadModel> {}
