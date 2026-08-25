package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarFichasPerfilInteractor
        extends Interactor<FichaPerfilCriteria, PaginatedResult<FichaPerfilReadModel>> {}
