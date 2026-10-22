package com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EvaluacionEstudianteQueryOutputPort {

    PaginatedResult<EvaluacionReadModel> consultarPorEstudiante(EvaluacionEstudianteCriteria criteria);
}
