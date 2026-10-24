package com.arquisoft.artefactos.application.revisionasesor.query.usecase;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarRevisionesAsesorAsesorUseCase
        extends UseCase<RevisionAsesorAsesorCriteria, PaginatedResult<RevisionAsesorReadModel>> {}
