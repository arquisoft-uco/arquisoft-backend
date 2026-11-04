package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesCualitativasAsesorInteractor
        extends Interactor<ConsultarEvaluacionesCualitativasAsesorQuery, List<EvaluacionCualitativaAsesorReadModel>> {
}
