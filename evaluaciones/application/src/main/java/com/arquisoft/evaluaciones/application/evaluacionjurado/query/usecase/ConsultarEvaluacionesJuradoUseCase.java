package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEvaluacionesJuradoUseCase
        extends UseCase<EvaluacionJuradoCriteria, PaginatedResult<EvaluacionJuradoReadModel>> {
}
