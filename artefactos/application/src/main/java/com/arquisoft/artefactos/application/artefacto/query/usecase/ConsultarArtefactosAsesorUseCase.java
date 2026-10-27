package com.arquisoft.artefactos.application.artefacto.query.usecase;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarArtefactosAsesorUseCase
        extends UseCase<ArtefactoAsesorCriteria, PaginatedResult<ArtefactoReadModel>> {}
