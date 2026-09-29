package com.arquisoft.usuarios.application.representantecomite.query.primaryport.interactor;

import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRepresentantesComiteAdministradorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<RepresentanteComiteReadModel>> {}
