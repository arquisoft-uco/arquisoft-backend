package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEvaluacionesAsignadasAsesorUseCase
        extends UseCase<EvaluacionesAsignadasAsesorCriteria, PaginatedResult<EvaluacionAsignadaAsesorReadModel>> {
}
