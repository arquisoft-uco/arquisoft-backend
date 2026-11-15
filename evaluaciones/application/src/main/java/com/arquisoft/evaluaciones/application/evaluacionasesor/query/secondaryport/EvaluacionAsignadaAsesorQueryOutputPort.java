package com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EvaluacionAsignadaAsesorQueryOutputPort {

    PaginatedResult<EvaluacionAsignadaAsesorReadModel> consultarTodas(EvaluacionesAsignadasAsesorCriteria criteria);
}
