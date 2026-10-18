package com.arquisoft.artefactos.application.revisionasesor.query.usecase;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarRevisionesAsesorEstudianteUseCase
        extends UseCase<RevisionAsesorEstudianteCriteria, PaginatedResult<RevisionAsesorReadModel>> {}
