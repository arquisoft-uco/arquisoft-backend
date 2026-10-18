package com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EvaluacionQueryOutputPort {

    PaginatedResult<EvaluacionReadModel> consultarTodas(EvaluacionCriteria criteria);
}
