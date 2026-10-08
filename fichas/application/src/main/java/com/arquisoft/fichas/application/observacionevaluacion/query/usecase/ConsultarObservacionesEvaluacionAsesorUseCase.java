package com.arquisoft.fichas.application.observacionevaluacion.query.usecase;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarObservacionesEvaluacionAsesorUseCase
        extends UseCase<ObservacionEvaluacionAsesorCriteria, List<ObservacionEvaluacionReadModel>> {}
