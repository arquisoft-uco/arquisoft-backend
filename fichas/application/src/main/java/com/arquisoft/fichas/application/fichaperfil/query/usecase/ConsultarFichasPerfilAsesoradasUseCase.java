package com.arquisoft.fichas.application.fichaperfil.query.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarFichasPerfilAsesoradasUseCase
        extends UseCase<FichaPerfilCriteria, PaginatedResult<FichaPerfilReadModel>> {}
