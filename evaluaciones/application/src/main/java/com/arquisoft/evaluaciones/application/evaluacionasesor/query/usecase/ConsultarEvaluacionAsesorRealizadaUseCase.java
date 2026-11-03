package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEvaluacionAsesorRealizadaUseCase
        extends UseCase<EvaluacionAsesorRealizadaCriteria, EvaluacionAsesorRealizadaReadModel> {}
