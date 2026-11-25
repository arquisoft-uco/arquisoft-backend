package com.arquisoft.fichas.application.fichaperfil.query.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarFichasPerfilRepresentanteUseCase
        extends UseCase<FichaPerfilRepresentanteCriteria, PaginatedResult<FichaPerfilRepresentanteReadModel>> {}
