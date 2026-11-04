package com.arquisoft.evaluaciones.application.evaluacion.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEvaluacionesCoordinadorUseCase
        extends UseCase<EvaluacionCriteria, PaginatedResult<EvaluacionReadModel>> {
}
