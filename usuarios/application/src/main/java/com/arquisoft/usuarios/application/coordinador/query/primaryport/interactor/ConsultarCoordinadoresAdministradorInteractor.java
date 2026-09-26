package com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor;

import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarCoordinadoresAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<CoordinadorReadModel>> {}
