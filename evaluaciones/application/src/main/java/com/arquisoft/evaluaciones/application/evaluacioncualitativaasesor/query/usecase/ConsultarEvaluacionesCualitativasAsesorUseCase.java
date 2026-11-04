package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesCualitativasAsesorUseCase
        extends UseCase<EvaluacionCualitativaAsesorCriteria, List<EvaluacionCualitativaAsesorReadModel>> {
}
