package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesFichaPerfilEstudianteInteractor
        extends Interactor<ConsultarEvaluacionesFichaPerfilEstudianteQuery, List<EvaluacionFichaPerfilEstudianteReadModel>> {}
