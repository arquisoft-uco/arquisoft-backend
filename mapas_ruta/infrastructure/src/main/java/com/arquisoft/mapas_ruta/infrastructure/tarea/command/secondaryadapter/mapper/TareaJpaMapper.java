package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;

public final class TareaJpaMapper {

    private TareaJpaMapper() {}

    public static TareaJpaEntity toJpaEntity(TareaEntity entity) {
        return TareaJpaEntity.builder()
                .id(entity.id())
                .mapaRutaId(entity.mapaRuta())
                .descripcion(entity.descripcion())
                .asesorId(entity.asesor())
                .estadoItemId(entity.estadoItem())
                .build();
    }
}
