package com.arquisoft.mapas_ruta.application.planreal.query.usecase;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarPlanesRealesProyectoGradoAsesorUseCase
        extends UseCase<PlanRealProyectoCriteria, PaginatedResult<PlanRealProyectoReadModel>> {}
