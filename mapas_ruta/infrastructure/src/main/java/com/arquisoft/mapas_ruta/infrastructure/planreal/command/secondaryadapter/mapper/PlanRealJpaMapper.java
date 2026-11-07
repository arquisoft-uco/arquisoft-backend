package com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity.PlanRealEntity;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.entity.PlanRealJpaEntity;

public final class PlanRealJpaMapper {

    private PlanRealJpaMapper() {}

    public static PlanRealJpaEntity toJpaEntity(PlanRealEntity entity) {
        return PlanRealJpaEntity.builder()
                .id(entity.id())
                .tareaId(entity.tarea())
                .tiempoRealDias(entity.tiempoRealDias())
                .descripcionReal(entity.descripcionReal().isEmpty() ? null : entity.descripcionReal())
                .build();
    }
}
