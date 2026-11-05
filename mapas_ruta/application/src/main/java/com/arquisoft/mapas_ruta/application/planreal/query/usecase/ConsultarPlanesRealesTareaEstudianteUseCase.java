package com.arquisoft.mapas_ruta.application.planreal.query.usecase;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarPlanesRealesTareaEstudianteUseCase
        extends UseCase<PlanRealTareaCriteria, PaginatedResult<PlanRealReadModel>> {}
