package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesCuantitativasJuradoInteractor
        extends Interactor<ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery, List<EvaluacionCuantitativaJuradoReadModel>> {
}
