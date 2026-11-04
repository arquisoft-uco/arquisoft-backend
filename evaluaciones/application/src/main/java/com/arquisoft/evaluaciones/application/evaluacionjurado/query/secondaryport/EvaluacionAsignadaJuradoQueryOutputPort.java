package com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EvaluacionAsignadaJuradoQueryOutputPort {

    PaginatedResult<EvaluacionAsignadaJuradoReadModel> consultarTodas(EvaluacionesAsignadasJuradoCriteria criteria);
}
