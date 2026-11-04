package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEvaluacionesAsignadasJuradoUseCase
        extends UseCase<EvaluacionesAsignadasJuradoCriteria, PaginatedResult<EvaluacionAsignadaJuradoReadModel>> {
}
