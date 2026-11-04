package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesCuantitativasAsesorInteractor
        extends Interactor<ConsultarEvaluacionesCuantitativasAsesorQuery, List<EvaluacionCuantitativaAsesorReadModel>> {
}
