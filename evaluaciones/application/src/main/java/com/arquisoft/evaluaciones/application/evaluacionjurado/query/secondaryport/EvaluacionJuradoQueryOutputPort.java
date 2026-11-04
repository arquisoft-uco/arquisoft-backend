package com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EvaluacionJuradoQueryOutputPort {

    PaginatedResult<EvaluacionJuradoReadModel> consultarTodas(EvaluacionJuradoCriteria criteria);
}
