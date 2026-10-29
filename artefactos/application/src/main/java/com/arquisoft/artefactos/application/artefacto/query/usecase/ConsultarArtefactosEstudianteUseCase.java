package com.arquisoft.artefactos.application.artefacto.query.usecase;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarArtefactosEstudianteUseCase
        extends UseCase<ArtefactoEstudianteCriteria, PaginatedResult<ArtefactoEstudianteReadModel>> {}
