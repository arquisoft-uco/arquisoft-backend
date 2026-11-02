package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEvaluacionAsesorUseCase
        extends UseCase<EvaluacionAsesorDeEvaluacionCriteria, EvaluacionAsesorReadModel> {}
