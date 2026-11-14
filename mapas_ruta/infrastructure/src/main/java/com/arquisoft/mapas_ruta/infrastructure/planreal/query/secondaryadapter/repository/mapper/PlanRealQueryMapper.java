package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.PlanRealJpaQueryEntity;

public final class PlanRealQueryMapper {

    private PlanRealQueryMapper() {}

    public static PlanRealReadModel toReadModel(PlanRealJpaQueryEntity entity) {
        return new PlanRealReadModel(
                entity.getId(),
                entity.getTareaId(),
                entity.getTiempoRealDias(),
                entity.getDescripcionReal());
    }
}
