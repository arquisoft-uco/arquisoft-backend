package com.arquisoft.usuarios.application.administrador.query.primaryport.interactor;

import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAdministradoresVigentesInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<AdministradorVigenteReadModel>> {}
