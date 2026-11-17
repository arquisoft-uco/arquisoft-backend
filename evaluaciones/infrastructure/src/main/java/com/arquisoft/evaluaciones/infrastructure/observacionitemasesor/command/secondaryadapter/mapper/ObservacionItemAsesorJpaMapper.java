package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.entity.ObservacionItemAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.entity.ObservacionItemAsesorJpaEntity;

public final class ObservacionItemAsesorJpaMapper {

    private ObservacionItemAsesorJpaMapper() {}

    public static ObservacionItemAsesorJpaEntity toJpaEntity(ObservacionItemAsesorEntity entity) {
        return ObservacionItemAsesorJpaEntity.builder()
                .id(entity.id())
                .evaluacionCuantitativaAsesorId(entity.evaluacionCuantitativaAsesor())
                .descripcion(entity.descripcion())
                .build();
    }
}
