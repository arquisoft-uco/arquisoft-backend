package com.arquisoft.fichas.application.estadofichaperfil.query.usecase;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEstadosFichaPerfilAsesorUseCase
        extends UseCase<EstadoFichaPerfilAsesorCriteria, PaginatedResult<EstadoFichaPerfilAsesorReadModel>> {}
