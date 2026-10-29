package com.arquisoft.mapas_ruta.application.planestimado.query.secondaryport;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface PlanEstimadoQueryOutputPort {

    PaginatedResult<PlanEstimadoReadModel> consultarPorTarea(PlanEstimadoTareaCriteria criteria);
}
