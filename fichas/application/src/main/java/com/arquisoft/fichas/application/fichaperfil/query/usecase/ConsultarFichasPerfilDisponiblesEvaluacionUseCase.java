package com.arquisoft.fichas.application.fichaperfil.query.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarFichasPerfilDisponiblesEvaluacionUseCase
        extends UseCase<FichaPerfilDisponibleEvaluacionCriteria, PaginatedResult<FichaPerfilReadModel>> {}
