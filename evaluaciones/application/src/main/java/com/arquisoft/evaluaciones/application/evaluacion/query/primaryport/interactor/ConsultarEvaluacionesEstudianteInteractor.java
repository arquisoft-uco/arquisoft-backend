package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEvaluacionesEstudianteInteractor
        extends Interactor<ConsultarEvaluacionesEstudianteQuery, PaginatedResult<EvaluacionReadModel>> {
}
