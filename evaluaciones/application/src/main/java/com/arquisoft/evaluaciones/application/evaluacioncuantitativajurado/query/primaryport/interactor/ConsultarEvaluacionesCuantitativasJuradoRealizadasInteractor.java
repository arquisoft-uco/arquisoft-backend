package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractor
        extends Interactor<ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery, List<EvaluacionCuantitativaJuradoReadModel>> {
}
