package com.arquisoft.usuarios.application.asesor.query.primaryport.interactor;

import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresVigentesInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<AsesorVigenteReadModel>> {}
