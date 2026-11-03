package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesCuantitativasAsesorUseCase
        extends UseCase<EvaluacionCuantitativaAsesorCriteria, List<EvaluacionCuantitativaAsesorReadModel>> {
}
