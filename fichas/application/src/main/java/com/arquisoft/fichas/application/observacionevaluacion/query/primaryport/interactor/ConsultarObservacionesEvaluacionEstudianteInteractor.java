package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionEstudianteQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarObservacionesEvaluacionEstudianteInteractor
        extends Interactor<ConsultarObservacionesEvaluacionEstudianteQuery, List<ObservacionEvaluacionReadModel>> {}
