package com.arquisoft.mapas_ruta.application.planreal.query.secondaryport;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface PlanRealQueryOutputPort {

    PaginatedResult<PlanRealReadModel> consultarPorTarea(PlanRealTareaCriteria criteria);
}
