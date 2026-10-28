package com.arquisoft.mapas_ruta.application.planestimado.query.usecase;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarPlanesEstimadosTareaEstudianteUseCase
        extends UseCase<PlanEstimadoTareaCriteria, PaginatedResult<PlanEstimadoReadModel>> {}
