package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesCualitativasJuradoUseCase
        extends UseCase<EvaluacionCualitativaJuradoCriteria, List<EvaluacionCualitativaJuradoReadModel>> {
}
