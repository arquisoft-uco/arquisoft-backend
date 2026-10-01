package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEvaluacionesFichaPerfilRepresentanteInteractor
        extends Interactor<ConsultarEvaluacionesFichaPerfilRepresentanteQuery, List<EvaluacionFichaPerfilReadModel>> {}
