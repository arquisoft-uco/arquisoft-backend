package com.arquisoft.fichas.application.fichaperfil.query.port.in;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.pagination.PaginatedResult;

public interface ConsultarFichasPerfilUseCase
        extends UseCase<FichaPerfilCriteria, PaginatedResult<FichaPerfilReadModel>> {}
