package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEvaluacionesAsesorInteractor
        extends Interactor<ConsultarEvaluacionesAsesorQuery, PaginatedResult<EvaluacionReadModel>> {
}
