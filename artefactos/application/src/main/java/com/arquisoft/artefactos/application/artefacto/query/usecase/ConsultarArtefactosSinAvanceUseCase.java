package com.arquisoft.artefactos.application.artefacto.query.usecase;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarArtefactosSinAvanceUseCase
        extends UseCase<ArtefactoSinAvanceCriteria, PaginatedResult<ArtefactoSinAvanceReadModel>> {}
