package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.PlanRealProyectoJpaQueryEntity;

public final class PlanRealProyectoQueryMapper {

    private PlanRealProyectoQueryMapper() {}

    public static PlanRealProyectoReadModel toReadModel(PlanRealProyectoJpaQueryEntity entity) {
        return new PlanRealProyectoReadModel(
                entity.getId(),
                entity.getTareaId(),
                entity.getDescripcionTarea(),
                entity.getMapaRutaId(),
                entity.getTiempoRealDias(),
                entity.getDescripcionReal());
    }
}
