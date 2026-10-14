package com.arquisoft.fichas.application.observacionevaluacion.query.usecase;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarObservacionesEvaluacionEstudianteUseCase
        extends UseCase<ObservacionEvaluacionEstudianteCriteria, List<ObservacionEvaluacionReadModel>> {}
