package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesCualitativasJuradoInteractor
        extends Interactor<ConsultarEvaluacionesCualitativasJuradoEstudianteQuery, List<EvaluacionCualitativaJuradoReadModel>> {
}
