package com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor;

import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarCoordinadoresVigentesInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<CoordinadorVigenteReadModel>> {}
