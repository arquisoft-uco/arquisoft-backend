package com.arquisoft.fichas.application.fichaperfil.query.primaryport.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarFichasPerfilUseCase
        extends UseCase<FichaPerfilCriteria, PaginatedResult<FichaPerfilReadModel>> {}
