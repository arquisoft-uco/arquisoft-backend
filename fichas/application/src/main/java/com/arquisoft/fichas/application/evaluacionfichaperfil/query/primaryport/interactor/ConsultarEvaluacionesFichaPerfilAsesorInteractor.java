package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEvaluacionesFichaPerfilAsesorInteractor
        extends Interactor<ConsultarEvaluacionesFichaPerfilAsesorQuery, PaginatedResult<EvaluacionFichaPerfilAsesorReadModel>> {}
