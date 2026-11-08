package com.arquisoft.mapas_ruta.application.planestimado.query.usecase;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarPlanesEstimadosProyectoGradoAsesorUseCase
        extends UseCase<PlanEstimadoProyectoCriteria, PaginatedResult<PlanEstimadoProyectoReadModel>> {}
