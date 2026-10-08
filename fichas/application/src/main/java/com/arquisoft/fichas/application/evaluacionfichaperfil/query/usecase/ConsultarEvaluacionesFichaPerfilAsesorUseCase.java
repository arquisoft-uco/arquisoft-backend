package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEvaluacionesFichaPerfilAsesorUseCase
        extends UseCase<EvaluacionFichaPerfilAsesorCriteria, PaginatedResult<EvaluacionFichaPerfilAsesorReadModel>> {}
