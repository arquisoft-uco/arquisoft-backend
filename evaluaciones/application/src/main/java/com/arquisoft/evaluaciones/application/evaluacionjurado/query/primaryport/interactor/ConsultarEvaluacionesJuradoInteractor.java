package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEvaluacionesJuradoInteractor
        extends Interactor<ConsultarEvaluacionesJuradoEstudianteQuery, PaginatedResult<EvaluacionJuradoReadModel>> {
}
