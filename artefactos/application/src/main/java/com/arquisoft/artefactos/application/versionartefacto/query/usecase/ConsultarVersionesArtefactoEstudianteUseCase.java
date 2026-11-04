package com.arquisoft.artefactos.application.versionartefacto.query.usecase;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarVersionesArtefactoEstudianteUseCase
        extends UseCase<VersionArtefactoEstudianteCriteria, PaginatedResult<VersionArtefactoReadModel>> {}
