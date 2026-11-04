package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEvaluacionesAsignadasJuradoInteractor extends
        Interactor<ConsultarEvaluacionesAsignadasJuradoQuery, PaginatedResult<EvaluacionAsignadaJuradoReadModel>> {
}
