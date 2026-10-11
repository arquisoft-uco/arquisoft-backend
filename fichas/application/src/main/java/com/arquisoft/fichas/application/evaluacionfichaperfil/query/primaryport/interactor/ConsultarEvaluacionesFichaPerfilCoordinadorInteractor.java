package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesFichaPerfilCoordinadorInteractor
        extends Interactor<ConsultarEvaluacionesFichaPerfilCoordinadorQuery, List<EvaluacionFichaPerfilCoordinadorReadModel>> {}
