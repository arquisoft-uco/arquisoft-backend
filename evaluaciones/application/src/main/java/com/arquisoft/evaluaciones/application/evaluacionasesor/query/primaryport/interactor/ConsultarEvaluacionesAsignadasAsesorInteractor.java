package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionesAsignadasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEvaluacionesAsignadasAsesorInteractor extends
        Interactor<ConsultarEvaluacionesAsignadasAsesorQuery, PaginatedResult<EvaluacionAsignadaAsesorReadModel>> {
}
