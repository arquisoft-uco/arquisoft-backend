package com.arquisoft.usuarios.application.asesor.query.primaryport.interactor;

import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<AsesorReadModel>> {}
