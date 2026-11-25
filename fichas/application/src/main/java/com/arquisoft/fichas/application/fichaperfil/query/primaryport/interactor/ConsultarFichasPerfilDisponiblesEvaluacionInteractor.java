package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilDisponiblesEvaluacionQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarFichasPerfilDisponiblesEvaluacionInteractor
        extends Interactor<ConsultarFichasPerfilDisponiblesEvaluacionQuery, PaginatedResult<FichaPerfilReadModel>> {}
