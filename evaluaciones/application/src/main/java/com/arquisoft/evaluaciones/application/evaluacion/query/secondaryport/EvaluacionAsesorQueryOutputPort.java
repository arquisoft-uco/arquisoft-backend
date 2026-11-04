package com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EvaluacionAsesorQueryOutputPort {

    PaginatedResult<EvaluacionReadModel> consultarPorAsesor(EvaluacionAsesorCriteria criteria);
}
