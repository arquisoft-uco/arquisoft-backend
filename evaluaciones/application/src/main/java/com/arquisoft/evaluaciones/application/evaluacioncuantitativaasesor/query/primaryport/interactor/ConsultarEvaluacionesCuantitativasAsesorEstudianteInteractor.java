package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractor
        extends Interactor<ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery,
                List<EvaluacionCuantitativaAsesorReadModel>> {
}
