package com.arquisoft.usuarios.application.administrador.query.primaryport.interactor;

import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAdministradoresAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<AdministradorReadModel>> {}
