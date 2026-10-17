package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEvaluacionesAsignadasJuradoUseCase
        extends UseCase<EvaluacionesAsignadasJuradoCriteria, List<EvaluacionAsignadaJuradoReadModel>> {
}
