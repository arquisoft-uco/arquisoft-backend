package com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EvaluacionDelJuradoQueryOutputPort {

    PaginatedResult<EvaluacionReadModel> consultarPorJurado(EvaluacionDelJuradoCriteria criteria);
}
