package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesCualitativasAsesorEstudianteUseCase
        extends UseCase<EvaluacionCualitativaAsesorEstudianteCriteria, List<EvaluacionCualitativaAsesorEstudianteReadModel>> {
}
