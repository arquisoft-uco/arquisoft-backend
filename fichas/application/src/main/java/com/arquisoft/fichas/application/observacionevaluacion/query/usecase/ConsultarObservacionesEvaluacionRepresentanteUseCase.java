package com.arquisoft.fichas.application.observacionevaluacion.query.usecase;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionRepresentanteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarObservacionesEvaluacionRepresentanteUseCase
        extends UseCase<ObservacionEvaluacionRepresentanteCriteria, List<ObservacionEvaluacionReadModel>> {}
