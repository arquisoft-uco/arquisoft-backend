package com.arquisoft.usuarios.application.representantecomite.query.primaryport.interactor;

import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRepresentantesComiteVigentesInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<RepresentanteComiteVigenteReadModel>> {}
