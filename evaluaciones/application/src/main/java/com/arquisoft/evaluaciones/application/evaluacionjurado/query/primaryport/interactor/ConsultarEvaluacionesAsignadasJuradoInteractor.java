package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesAsignadasJuradoInteractor
        extends Interactor<ConsultarEvaluacionesAsignadasJuradoQuery, List<EvaluacionAsignadaJuradoReadModel>> {
}
