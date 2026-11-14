package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCase
        extends UseCase<EvaluacionCuantitativaAsesorEstudianteCriteria, List<EvaluacionCuantitativaAsesorReadModel>> {
}
