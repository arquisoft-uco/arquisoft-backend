package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesCuantitativasJuradoUseCase
        extends UseCase<EvaluacionCuantitativaJuradoCriteria, List<EvaluacionCuantitativaJuradoReadModel>> {
}
